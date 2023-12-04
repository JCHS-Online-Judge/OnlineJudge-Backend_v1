package com.github.ioloolo.onlinejudge.domain.judge.controller.websocket;

import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SubjectAllHandler extends TextWebSocketHandler {

    private final JudgeRepository judgeRepository;

    @Getter
    private static final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {

        sessions.remove(session);
    }
}
