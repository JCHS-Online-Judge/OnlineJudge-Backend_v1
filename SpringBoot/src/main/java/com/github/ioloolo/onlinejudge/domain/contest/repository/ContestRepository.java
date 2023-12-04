package com.github.ioloolo.onlinejudge.domain.contest.repository;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContestRepository extends MongoRepository<Contest, String> {

    Optional<Contest> findByInviteCode(String inviteCode);

    boolean existsByTitle(String title);

    boolean existsByInviteCode(String inviteCode);
}
