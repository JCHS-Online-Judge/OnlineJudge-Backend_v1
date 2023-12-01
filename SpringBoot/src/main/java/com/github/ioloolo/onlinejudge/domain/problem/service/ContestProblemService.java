package com.github.ioloolo.onlinejudge.domain.problem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.repository.ContestRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContestProblemService {

	private final ProblemRepository repository;
	private final ContestRepository contestRepository;

	public List<Problem.Simple> getProblems(String contestId, User user) throws Exception {

		Optional<Contest> contestOptional = contestRepository.findById(contestId);
		if (contestOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Contest contest = contestOptional.get();

		if (!user.isAdmin() && !contest.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}

		return repository.findAll()
				.stream()
				.filter(Problem::isContest)
				.filter(problem -> problem.getContest().equals(contest))
				.map(Problem::toSimple)
				.toList();
	}

	public Problem getProblemInfo(String problemId, User user) throws Exception {

		Optional<Problem> problemOptional = repository.findById(problemId);
		if (problemOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NOT_FOUND);
		}
		Problem problem = problemOptional.get();

		if (!problem.isContest()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}
		if (!user.isAdmin() && !problem.getContest().getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		problem.setTestCases(problem.getTestCases().stream().filter(Problem.TestCase::isSample).toList());

		return problem;
	}

	public void createProblem(
			String contestId,
			String problemNumber,
			String title,
			String description,
			String inputDescription,
			String outputDescription,
			long timeLimit,
			long memoryLimit,
			List<Problem.TestCase> testCases
	) throws Exception {

		Optional<Contest> contestOptional = contestRepository.findById(contestId);
		if (contestOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Contest contest = contestOptional.get();

		if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, null, contest)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
		}
		if (repository.existsByTitleAndLectureAndContest(title, null, contest)) {
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
				.contest(contest)
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

		Optional<Problem> problemOptional = repository.findById(problemId);
		if (problemOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NOT_FOUND);
		}
		Problem problem = problemOptional.get();

		Contest contest = problem.getContest();

		if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, null, contest)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
		}
		if (repository.existsByTitleAndLectureAndContest(title, null, contest)) {
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

		Optional<Problem> problemOptional = repository.findById(problemId);
		if (problemOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NOT_FOUND);
		}
		Problem problem = problemOptional.get();

		if (!problem.isContest()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}

		repository.delete(problem);
	}
}
