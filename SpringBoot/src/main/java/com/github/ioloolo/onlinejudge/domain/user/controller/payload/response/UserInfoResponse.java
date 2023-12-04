package com.github.ioloolo.onlinejudge.domain.user.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import java.util.Map;

public class UserInfoResponse extends KVResponse {

    public UserInfoResponse(User user) {

        super(Map.of("user", user));
    }
}
