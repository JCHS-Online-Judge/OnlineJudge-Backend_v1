package com.github.ioloolo.onlinejudge.domain.rank.controller;

import com.github.ioloolo.onlinejudge.domain.rank.controller.payload.response.RankResponse;
import com.github.ioloolo.onlinejudge.domain.rank.data.Rank;
import com.github.ioloolo.onlinejudge.domain.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
public class RankController {

    private final RankService service;

    @PostMapping
    public ResponseEntity<RankResponse> getLectureInfo() throws Exception {

        List<Rank> rank = service.getRank();

        return ResponseEntity.ok(new RankResponse(rank));
    }
}
