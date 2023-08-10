package com.github.ioloolo.onlinejudge.domain.problem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemIdAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemNotExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemTitleAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProblemService {

	private final ProblemRepository repository;

	public List<Problem> getProblemList() {
		return repository.findAll()
				.stream()
				.peek(problem -> problem.setTestCases(List.of()))
				.toList();
	}

	public Optional<Problem> getProblem(String id) {
		Optional<Problem> problemOptional = repository.findByProblemId(id);

		if (problemOptional.isPresent()) {
			Problem problem = problemOptional.get();
			problem.setTestCases(List.of());

			return Optional.of(problem);
		} else {
			return Optional.empty();
		}
	}

	public void createProblem(Problem problem) throws
			ProblemIdAlreadyExistException,
			ProblemTitleAlreadyExistException {

		if (repository.existsByProblemId(problem.getProblemId())) {
			throw new ProblemIdAlreadyExistException(problem);
		}

		if (repository.existsByTitle(problem.getTitle())) {
			throw new ProblemTitleAlreadyExistException(problem);
		}

		repository.save(problem);
	}

	public void editProblem(String id, Problem changed) throws
			ProblemNotExistException,
			ProblemTitleAlreadyExistException,
			ProblemIdAlreadyExistException {

		Problem originProblem = repository.findByProblemId(id)
				.orElseThrow(() -> new ProblemNotExistException(id));

		if (!originProblem.getProblemId().equals(changed.getProblemId())
				&& repository.existsByProblemId(changed.getProblemId())) {

			throw new ProblemIdAlreadyExistException(changed);
		}

		if (!originProblem.getTitle().equals(changed.getTitle())
				&& repository.existsByTitle(changed.getTitle())) {

			throw new ProblemTitleAlreadyExistException(changed);
		}

		changed.setId(originProblem.getId());

		repository.save(changed);
	}

	public void removeProblem(String id) throws ProblemNotExistException {
		if (!repository.existsByProblemId(id)) {
			throw new ProblemNotExistException(id);
		}

		repository.deleteByProblemId(id);
	}
}
