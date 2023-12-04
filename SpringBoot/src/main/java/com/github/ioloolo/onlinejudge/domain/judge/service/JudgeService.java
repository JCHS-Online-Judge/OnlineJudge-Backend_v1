package com.github.ioloolo.onlinejudge.domain.judge.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.repository.ContestRepository;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeLanguage;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeResult;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class JudgeService {

    private final JudgeRepository repository;
    private final ProblemRepository problemRepository;
    private final LectureRepository lectureRepository;
    private final ContestRepository contestRepository;

    private final ExecutorService executorService;

    private final String containerName;

    private final DockerClient docker = DockerClientImpl.getInstance(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("unix:///var/run/docker.sock")
                    .build(),
            new ZerodepDockerHttpClient.Builder().dockerHost(URI.create("unix:///var/run/docker.sock"))
                    .build()
    );
    
    private final int AMOUNT_PER_PAGE = 25;

    public JudgeService(
            JudgeRepository repository,
            ProblemRepository problemRepository,
            LectureRepository lectureRepository,
            ContestRepository contestRepository,
            @Value("${app.judge.threadPoolSize}") int threadPoolSize,
            @Value("${app.judge.containerName}") String containerName
    ) {

        this.repository = repository;
        this.problemRepository = problemRepository;
        this.lectureRepository = lectureRepository;
        this.contestRepository = contestRepository;

        this.executorService = Executors.newFixedThreadPool(threadPoolSize);

        this.containerName = containerName + ":latest";
    }

    @PostConstruct
    public void init() throws Exception {

        System.out.println(containerName + ":latest");
        boolean existsContainer = docker.listImagesCmd()
                .exec()
                .stream()
                .map(Image::getRepoTags)
                .anyMatch(tags -> Arrays.asList(tags).contains(containerName));

        if (!existsContainer) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTAINER_NOT_FOUND);
        }
    }

    public JudgeHistory judge(String problemId, JudgeLanguage language, String sourceCode, User user) throws Exception {

        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        if (problemOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NOT_FOUND);
        }
        Problem problem = problemOptional.get();

        JudgeHistory history = JudgeHistory.builder()
                .problem(problem)
                .language(language)
                .sourceCode(sourceCode)
                .user(user)
                .result(JudgeResult.READY)
                .createdTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .data(Map.of())
                .build();

        history = repository.save(history);

        Problem.Simple simple = history.getProblem().toSimple();
        history.setProblem(Problem.builder()
                .id(simple.getId())
                .problemNumber(simple.getProblemNumber())
                .title(simple.getTitle())
                .build());

        JudgeHistory finalHistory = history;
        executorService.submit(() -> {
            CreateContainerResponse containerInfo = docker.createContainerCmd(containerName)
                    .withName("judge-" + finalHistory.getId())
                    .withEnv("JUDGE_ID=" + finalHistory.getId())
                    .exec();

            docker.startContainerCmd(containerInfo.getId()).exec();

            docker.waitContainerCmd(containerInfo.getId()).start().awaitStatusCode();

            docker.removeContainerCmd(containerInfo.getId()).exec();
        });

        return history;
    }

    public JudgeHistory getInfo(String judgeId, User user) throws Exception {

        Optional<JudgeHistory> historyOptional = repository.findById(judgeId);
        if (historyOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.HISTORY_NOT_FOUND);
        }
        JudgeHistory history = historyOptional.get();

        if (!user.isAdmin() && !history.getUser().equals(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
        }

        Problem.Simple simple = history.getProblem().toSimple();
        history.setProblem(Problem.builder()
                .id(simple.getId())
                .problemNumber(simple.getProblemNumber())
                .title(simple.getTitle())
                .build());

        return history;
    }

    public List<JudgeHistory> getHistory(int page, User user) {

        return repository.findAll()
                .stream()
                .filter(history -> history.getProblem().isCommon())
                .limit(AMOUNT_PER_PAGE)
                .skip((long) page * AMOUNT_PER_PAGE)
                .peek(history -> {
                    if (!user.isAdmin() && !history.getUser().equals(user)) {
                        Problem problem = history.getProblem();
                        Problem.Simple simple = problem.toSimple();

                        history.setSourceCode(null);
                        history.setProblem(Problem.builder()
                                .id(simple.getId())
                                .problemNumber(simple.getProblemNumber())
                                .title(simple.getTitle())
                                .build());
                    }
                })
                .toList();
    }

    public int getHistoryMaxPage() {

        long count = repository.findAll().stream().filter(history -> history.getProblem().isCommon()).count();

        return (int) Math.ceil((double) count / AMOUNT_PER_PAGE);
    }

    public List<JudgeHistory> getLectureHistory(String lectureId, int page, User user) throws Exception {

        Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
        if (lectureOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
        }
        Lecture lecture = lectureOptional.get();

        return repository.findAll()
                .stream()
                .filter(history -> history.getProblem().isLecture())
                .filter(history -> history.getProblem().getLecture().equals(lecture))
                .limit(AMOUNT_PER_PAGE)
                .skip((long) page * AMOUNT_PER_PAGE)
                .peek(history -> {
                    if (!user.isAdmin() && !history.getUser().equals(user)) {
                        Problem problem = history.getProblem();
                        Problem.Simple simple = problem.toSimple();

                        history.setSourceCode(null);
                        history.setProblem(Problem.builder()
                                .id(simple.getId())
                                .problemNumber(simple.getProblemNumber())
                                .title(simple.getTitle())
                                .build());
                    }
                })
                .toList();
    }

    public int getLectureHistoryMaxPage(String lectureId) throws Exception {

        Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
        if (lectureOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
        }
        Lecture lecture = lectureOptional.get();

        long count = repository.findAll()
                .stream()
                .filter(history -> history.getProblem().isLecture())
                .filter(history -> history.getProblem().getLecture().equals(lecture))
                .count();

        return (int) Math.ceil((double) count / AMOUNT_PER_PAGE);
    }

    public List<JudgeHistory> getContestHistory(String contestId, int page, User user) throws Exception {

        Optional<Contest> contestOptional = contestRepository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        return repository.findAll()
                .stream()
                .filter(history -> history.getProblem().isContest())
                .filter(history -> history.getProblem().getContest().equals(contest))
                .limit(AMOUNT_PER_PAGE)
                .skip((long) page * AMOUNT_PER_PAGE)
                .peek(history -> {
                    if (!user.isAdmin() && !history.getUser().equals(user)) {
                        Problem problem = history.getProblem();
                        Problem.Simple simple = problem.toSimple();

                        history.setSourceCode(null);
                        history.setProblem(Problem.builder()
                                .id(simple.getId())
                                .problemNumber(simple.getProblemNumber())
                                .title(simple.getTitle())
                                .build());
                    }
                })
                .toList();
    }

    public int getContestHistoryMaxPage(String contestId) throws Exception {

        Optional<Contest> contestOptional = contestRepository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        long count = repository.findAll()
                .stream()
                .filter(history -> history.getProblem().isContest())
                .filter(history -> history.getProblem().getContest().equals(contest))
                .count();

        return (int) Math.ceil((double) count / AMOUNT_PER_PAGE);
    }
}
