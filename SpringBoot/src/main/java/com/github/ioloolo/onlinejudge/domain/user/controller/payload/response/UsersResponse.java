package com.github.ioloolo.onlinejudge.domain.user.controller.payload.response;

import java.util.List;
import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

public class UsersResponse extends KVResponse {

	public UsersResponse(List<User> users) {

		super(Map.of("users", users));
	}
}
