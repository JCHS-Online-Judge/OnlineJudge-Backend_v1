package com.github.ioloolo.onlinejudge.common.websocket;

import com.github.ioloolo.onlinejudge.common.logger.WebSocketLogging;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.SenderHandler;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.SubjectAllHandler;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.SubjectContestHandler;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.SubjectEachHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SenderHandler senderHandler;
    private final SubjectAllHandler subjectAllHandler;
    private final SubjectContestHandler subjectContestHandler;
    private final SubjectEachHandler subjectEachHandler;

    private final WebSocketLogging webSocketLogging;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(senderHandler, "/ws/sender")
                .addHandler(subjectAllHandler, "/ws/subject/all")
                .addHandler(subjectContestHandler, "/ws/subject/contest")
                .addHandler(subjectEachHandler, "/ws/subject/each")
                .addInterceptors(webSocketLogging);
    }
}
