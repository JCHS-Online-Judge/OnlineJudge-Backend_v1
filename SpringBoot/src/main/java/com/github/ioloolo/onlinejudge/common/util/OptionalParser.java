package com.github.ioloolo.onlinejudge.common.util;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;

import java.util.Optional;

public class OptionalParser {

    public static <T> T parse(Optional<T> optional, ExceptionFactory.Type type) throws Exception {
        if (optional.isEmpty()) {
            throw ExceptionFactory.of(type);
        }

        return optional.get();
    }
}
