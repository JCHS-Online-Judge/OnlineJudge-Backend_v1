package com.github.ioloolo.onlinejudge.domain.board.repository;

import com.github.ioloolo.onlinejudge.domain.board.data.BoardFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardFileRepository extends MongoRepository<BoardFile, String> {
}
