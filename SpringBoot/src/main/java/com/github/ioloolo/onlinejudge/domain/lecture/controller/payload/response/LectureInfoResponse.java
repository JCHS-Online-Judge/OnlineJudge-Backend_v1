package com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;

import java.util.Map;

public class LectureInfoResponse extends KVResponse {

    public LectureInfoResponse(Lecture lecture) {

        super(false, Map.of("lecture", lecture));
    }
}
