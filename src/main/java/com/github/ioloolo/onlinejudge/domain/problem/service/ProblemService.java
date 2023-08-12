package com.github.ioloolo.onlinejudge.domain.problem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.config.security.services.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.domain.auth.model.Role;
import com.github.ioloolo.onlinejudge.domain.auth.model.User;
import com.github.ioloolo.onlinejudge.domain.auth.repository.RoleRepository;
import com.github.ioloolo.onlinejudge.domain.auth.repository.UserRepository;
import com.github.ioloolo.onlinejudge.domain.judge.model.Judge;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemIdAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemNotExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemTitleAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProblemService {

	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final JudgeRepository judgeRepository;
	private final RoleRepository roleRepository;

	public List<Problem> getProblemList() {
		return problemRepository.findAll()
				.stream()
				.peek(problem -> problem.setTestCases(List.of()))
				.toList();
	}

	public Optional<Problem> getProblem(String id, UserDetailsImpl user) {
		Optional<Problem> problemOptional = problemRepository.findByProblemId(id);

		if (problemOptional.isPresent()) {
			Problem problem = problemOptional.get();

			User user1 = userRepository.findByUsername(user.getUsername()).orElseThrow();
			Role role = roleRepository.findByName(Role.Roles.ROLE_ADMIN).orElseThrow();

			if (!user1.getRoles().contains(role)) {
				problem.setTestCases(
						problem.getTestCases()
								.stream()
								.filter(Problem.TestCase::isExample)
								.toList()
				);
			}

			return Optional.of(problem);
		} else {
			return Optional.empty();
		}
	}

	public void createProblem(Problem problem) throws
			ProblemIdAlreadyExistException,
			ProblemTitleAlreadyExistException {

		if (problemRepository.existsByProblemId(problem.getProblemId())) {
			throw new ProblemIdAlreadyExistException(problem);
		}

		if (problemRepository.existsByTitle(problem.getTitle())) {
			throw new ProblemTitleAlreadyExistException(problem);
		}

		problemRepository.save(problem);
	}

	public void editProblem(String id, Problem changed) throws
			ProblemNotExistException,
			ProblemTitleAlreadyExistException,
			ProblemIdAlreadyExistException {

		Problem originProblem = problemRepository.findByProblemId(id)
				.orElseThrow(() -> new ProblemNotExistException(id));

		if (!originProblem.getProblemId().equals(changed.getProblemId())
				&& problemRepository.existsByProblemId(changed.getProblemId())) {

			throw new ProblemIdAlreadyExistException(changed);
		}

		if (!originProblem.getTitle().equals(changed.getTitle())
				&& problemRepository.existsByTitle(changed.getTitle())) {

			throw new ProblemTitleAlreadyExistException(changed);
		}

		changed.setId(originProblem.getId());

		problemRepository.save(changed);
	}

	public void removeProblem(String id) throws ProblemNotExistException {
		if (!problemRepository.existsByProblemId(id)) {
			throw new ProblemNotExistException(id);
		}

		Problem problem = problemRepository.findByProblemId(id).orElseThrow();

		for (Judge judge : judgeRepository.findAllByProblem(problem)) {
			judgeRepository.delete(judge);
		}

		problemRepository.deleteByProblemId(id);
	}
}
