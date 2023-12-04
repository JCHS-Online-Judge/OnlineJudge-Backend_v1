package com.github.ioloolo.onlinejudge.domain.problem.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.repository.ProblemRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureProblemService {

    private final ProblemRepository repository;
    private final LectureRepository lectureRepository;

    public List<Problem.Simple> getProblems(String lectureId, User user) throws Exception {

        Lecture lecture = OptionalParser.parse(lectureRepository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        if (!user.isAdmin() && !lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
        }

        return lecture.getProblems().stream()
                .map(Problem::toSimple)
                .toList();
    }

    public void addProblem(String lectureId, String problemId) throws Exception {

        Lecture lecture = OptionalParser.parse(lectureRepository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);
        Problem problem = OptionalParser.parse(repository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        List<Problem> problems = lecture.getProblems();

        if (problems.contains(problem)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_LECTURE_PROBLEM);
        }

        problems.add(problem);

        lectureRepository.save(lecture);
    }

    public void deleteProblem(String lectureId, String problemId) throws Exception {

        Lecture lecture = OptionalParser.parse(lectureRepository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);
        Problem problem = OptionalParser.parse(repository.findById(problemId), ExceptionFactory.Type.PROBLEM_NOT_FOUND);

        List<Problem> problems = lecture.getProblems();

        if (!problems.contains(problem)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.NOT_EXIST_LECTURE_PROBLEM);
        }

        problems.remove(problem);

        lectureRepository.save(lecture);
    }
}
