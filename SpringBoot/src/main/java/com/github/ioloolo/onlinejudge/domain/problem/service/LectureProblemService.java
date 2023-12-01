package com.github.ioloolo.onlinejudge.domain.problem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureProblemService {

	private final ProblemRepository repository;
	private final LectureRepository lectureRepository;

	public List<Problem.Simple> getProblems(String lectureId, User user) throws Exception {

		Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (!user.isAdmin() && !lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		return repository.findAll()
				.stream()
				.filter(Problem::isLecture)
				.filter(problem -> problem.getLecture().equals(lecture))
				.map(Problem::toSimple)
				.toList();
	}

	public Problem getProblemInfo(String problemId, User user) throws Exception {

		Optional<Problem> problemOptional = repository.findById(problemId);
		if (problemOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NOT_FOUND);
		}
		Problem problem = problemOptional.get();

		if (!problem.isLecture()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}
		if (!user.isAdmin() && !problem.getLecture().getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		problem.setTestCases(problem.getTestCases().stream().filter(Problem.TestCase::isSample).toList());

		return problem;
	}

	public void createProblem(
			String lectureId,
			String problemNumber,
			String title,
			String description,
			String inputDescription,
			String outputDescription,
			long timeLimit,
			long memoryLimit,
			List<Problem.TestCase> testCases
	) throws Exception {

		Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, lecture, null)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
		}
		if (repository.existsByTitleAndLectureAndContest(title, lecture, null)) {
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
				.lecture(lecture)
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

		Lecture lecture = problem.getLecture();

		if (repository.existsByProblemNumberAndLectureAndContest(problemNumber, lecture, null)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.PROBLEM_NUMBER_ALREADY_EXISTS);
		}
		if (repository.existsByTitleAndLectureAndContest(title, lecture, null)) {
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

		if (!problem.isLecture()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}

		repository.delete(problem);
	}
}
