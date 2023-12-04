package com.github.ioloolo.onlinejudge.domain.rank.service;

import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeResult;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.rank.data.Rank;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository repository;
    private final JudgeRepository judgeRepository;

    public Set<Problem> getSolvedProblems(User user) {

        return judgeRepository.findAllByUserAndResult(user, JudgeResult.ACCEPT)
                .stream()
                .collect(Collectors.groupingBy(JudgeHistory::getProblem))
                .keySet();
    }

    public List<Rank> getRank() {

        return repository.findAll()
                .stream()
                .map(user -> Rank.builder().user(user).solved(getSolvedProblems(user).size()).build())
                .sorted((o1, o2) -> {
                    int solved1 = o1.getSolved();
                    int solved2 = o2.getSolved();

                    return Integer.compare(solved2, solved1);
                })
                .toList();
    }
}
