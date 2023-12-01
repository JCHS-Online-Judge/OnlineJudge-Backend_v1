package com.github.ioloolo.onlinejudge.domain.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.board.data.BoardFile;

public interface BoardFileRepository extends MongoRepository<BoardFile, String> {
}
