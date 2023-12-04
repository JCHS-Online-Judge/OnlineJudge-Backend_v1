package com.github.ioloolo.onlinejudge.domain.problem.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository repository;

    public List<Problem.Simple> getProblems() {

        return repository.findAll().stream().filter(Problem::isCommon).map(Problem::toSimple).toList();
    }

    public Problem getProblemInfo(String problemId) throws Exception {

        Problem problem = OptionalParser.parse(repository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        if (!problem.isCommon()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        problem.setTestCases(problem.getTestCases().stream().filter(Problem.TestCase::isSample).toList());

        return problem;
    }

    public void createProblem(
            String problemNumber,
            String title,
            String description,
            String inputDescription,
            String outputDescription,
            long timeLimit,
            long memoryLimit,
            List<Problem.TestCase> testCases
    ) throws Exception {

        if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, null, null)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
        }
        if (repository.existsByTitleAndLectureAndContest(title, null, null)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_TITLE_ALREADY_EXISTS);
        }

        Problem problem = Problem.builder()
                .problemNumber(problemNumber)
                .title(title)
                .description(description)
                .inputDescription(inputDescription)
                .outputDescription(outputDescription)
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .testCases(testCases)
                .build();

        repository.save(problem);
    }

    public void updateProblem(
            String problemId,
            String problemNumber,
            String title,
            String description,
            String inputDescription,
            String outputDescription,
            long timeLimit,
            long memoryLimit,
            List<Problem.TestCase> testCases
    ) throws Exception {

        Problem problem = OptionalParser.parse(repository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, null, null)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
        }
        if (repository.existsByTitleAndLectureAndContest(title, null, null)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_TITLE_ALREADY_EXISTS);
        }

        problem.setProblemNumber(problemNumber);
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setInputDescription(inputDescription);
        problem.setOutputDescription(outputDescription);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setTestCases(testCases);

        repository.save(problem);
    }

    public void deleteProblem(String problemId) throws Exception {

        Problem problem = OptionalParser.parse(repository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        if (!problem.isCommon()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        repository.delete(problem);
    }
}
