package com.github.ioloolo.onlinejudge.common.payload.response;

import lombok.Data;

@Data
public class Response<T> {

    private final boolean isError;
    private final T content;

    public Response(T content) {
 
        this.isError = false;
        this.content = content;
    }

    public Response(boolean isError, T content) {

        this.isError = isError;
        this.content = content;
    }
}
