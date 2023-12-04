package com.github.ioloolo.onlinejudge.domain.board.repository;

import com.github.ioloolo.onlinejudge.domain.board.data.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
}
