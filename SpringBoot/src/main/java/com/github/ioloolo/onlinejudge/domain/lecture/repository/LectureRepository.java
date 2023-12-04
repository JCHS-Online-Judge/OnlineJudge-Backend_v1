package com.github.ioloolo.onlinejudge.domain.lecture.repository;

import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LectureRepository extends MongoRepository<Lecture, String> {

    Optional<Lecture> findByInviteCode(String inviteCode);

    boolean existsByTitle(String title);

    boolean existsByInviteCode(String inviteCode);
}
