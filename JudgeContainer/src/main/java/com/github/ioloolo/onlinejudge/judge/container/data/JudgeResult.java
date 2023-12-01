package com.github.ioloolo.onlinejudge.judge.container.data;

public enum JudgeResult {
	READY, PROCESSING,

	COMPILE_ERROR, RUNTIME_ERROR,

	ACCEPT, WRONG_ANSWER,

	TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED,
}
