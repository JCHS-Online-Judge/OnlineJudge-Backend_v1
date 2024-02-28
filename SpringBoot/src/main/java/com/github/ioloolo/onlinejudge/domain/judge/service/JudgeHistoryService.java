package com.github.ioloolo.onlinejudge.domain.judge.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.repository.ContestRepository;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JudgeHistoryService {

    private final JudgeRepository repository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ContestRepository contestRepository;

    private final int PAGE_SIZE = 25;

    public Map.Entry<List<JudgeHistory>, Integer> getHistory(int page) {

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }

    public Map.Entry<List<JudgeHistory>, Integer> getHistoryWithUserFilter(String userId, int page) throws Exception {

        User user = OptionalParser.parse(userRepository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .filter(history -> history.getUser().equals(user))
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }

    public Map.Entry<List<JudgeHistory>, Integer> getHistoryWithProblemFilter(String problemId, int page) throws Exception {

        Problem problem = OptionalParser.parse(problemRepository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .filter(history -> history.getProblem().equals(problem))
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }

    public Map.Entry<List<JudgeHistory>, Integer> getContestHistory(String contestId, int page) throws Exception {

        Contest contest = OptionalParser.parse(contestRepository.findById(contestId), ExceptionFactory.Type.CONTEST_NOT_FOUND);

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .filter(history -> history.getProblem().getContest().equals(contest))
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }

    public Map.Entry<List<JudgeHistory>, Integer> getContestHistoryWithUserFilter(String contestId, String userId, int page) throws Exception {

        Contest contest = OptionalParser.parse(contestRepository.findById(contestId), ExceptionFactory.Type.CONTEST_NOT_FOUND);

        User user = OptionalParser.parse(userRepository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .filter(history -> history.getProblem().getContest().equals(contest))
                .filter(history -> history.getUser().equals(user))
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }

    public Map.Entry<List<JudgeHistory>, Integer> getContestHistoryWithProblemFilter(String contestId, String problemId, int page) throws Exception {

        Contest contest = OptionalParser.parse(contestRepository.findById(contestId), ExceptionFactory.Type.CONTEST_NOT_FOUND);

        Problem problem = OptionalParser.parse(problemRepository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        List<JudgeHistory> histories = repository.findAllByOrderByCreatedTimeAsc()
                .stream()
                .filter(history -> history.getProblem().getContest().equals(contest))
                .filter(history -> history.getProblem().equals(problem))
                .skip((long) (page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        int maxPage = (int) Math.ceil((double) histories.size() / PAGE_SIZE);

        return Map.entry(histories, maxPage);
    }
}
