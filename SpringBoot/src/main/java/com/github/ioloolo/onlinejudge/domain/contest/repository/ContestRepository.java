package com.github.ioloolo.onlinejudge.domain.contest.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;

public interface ContestRepository extends MongoRepository<Contest, String> {

	Optional<Contest> findByInviteCode(String inviteCode);

	boolean existsByTitle(String title);

	boolean existsByInviteCode(String inviteCode);
}
