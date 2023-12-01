package com.github.ioloolo.onlinejudge.domain.lecture.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;

public interface LectureRepository extends MongoRepository<Lecture, String> {

	Optional<Lecture> findByInviteCode(String inviteCode);

	boolean existsByTitle(String title);

	boolean existsByInviteCode(String inviteCode);
}
