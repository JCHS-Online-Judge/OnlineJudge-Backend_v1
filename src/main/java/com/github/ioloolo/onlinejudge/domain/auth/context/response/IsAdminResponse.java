package com.github.ioloolo.onlinejudge.domain.auth.context.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class IsAdminResponse {
	private final boolean isAdmin;
}
