package com.github.ioloolo.onlinejudge.domain.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.board.data.Board;

public interface BoardRepository extends MongoRepository<Board, String> {
}
