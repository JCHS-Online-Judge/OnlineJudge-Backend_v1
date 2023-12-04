package com.github.ioloolo.onlinejudge.common.websocket;

import com.github.ioloolo.onlinejudge.common.logger.WebSocketLogging;
import com.github.ioloolo.onlinejudge.domain.judge.websocket.AllHandler;
import com.github.ioloolo.onlinejudge.domain.judge.websocket.EachHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final EachHandler eachHandler;
    private final AllHandler allHandler;

    private final WebSocketLogging webSocketLogging;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(eachHandler, "/api/ws/each")
                .addHandler(allHandler, "/api/ws/all")
                .addInterceptors(webSocketLogging);
    }
}
