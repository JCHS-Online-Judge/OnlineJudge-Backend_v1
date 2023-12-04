package com.github.ioloolo.onlinejudge.common.logger;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class WebSocketLogging implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            @Nonnull ServerHttpRequest request,
            @Nonnull ServerHttpResponse response,
            @Nonnull WebSocketHandler wsHandler,
            @Nonnull Map<String, Object> attributes
    ) throws Exception {

        return true;
    }

    @Override
    public void afterHandshake(
            @Nonnull ServerHttpRequest request,
            @Nonnull ServerHttpResponse response,
            @Nonnull WebSocketHandler wsHandler,
            Exception exception
    ) {

        String url = "%-20s".formatted(request.getURI().getPath());
        String remoteAddress = "%s".formatted(request.getRemoteAddress().getHostName());

        log.info("[WebSocket] {} {}", url, remoteAddress);
    }
}
