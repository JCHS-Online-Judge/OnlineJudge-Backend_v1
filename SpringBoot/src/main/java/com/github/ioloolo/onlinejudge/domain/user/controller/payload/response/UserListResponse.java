package com.github.ioloolo.onlinejudge.domain.user.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import java.util.List;
import java.util.Map;

public class UserListResponse extends KVResponse {

    public UserListResponse(List<User> users) {

        super(Map.of("users", users));
    }
}
