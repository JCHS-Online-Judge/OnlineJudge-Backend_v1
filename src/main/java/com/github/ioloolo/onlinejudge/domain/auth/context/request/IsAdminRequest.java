package com.github.ioloolo.onlinejudge.domain.auth.context.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IsAdminRequest {

	@NotBlank
	private String username;

}
