package com.github.ioloolo.onlinejudge.domain.judge.controller.websocket;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.header.WebSocketCustomHeader;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SubjectEachHandler extends TextWebSocketHandler {

    private final JudgeRepository judgeRepository;

    @Getter
    private static final Map<JudgeHistory, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

        HttpHeaders header = session.getHandshakeHeaders();

        String judgeId = OptionalParser.parse(Optional.ofNullable(header.getFirst(WebSocketCustomHeader.JUDGE_ID)), ExceptionFactory.Type.INVALID_HEADER);

        JudgeHistory history = OptionalParser.parse(judgeRepository.findById(judgeId), ExceptionFactory.Type.HISTORY_NOT_FOUND);

        Map<String, Object> attributes = session.getAttributes();
        attributes.put(WebSocketCustomHeader.JUDGE, history);

        if (!sessions.containsKey(history)) {
            sessions.put(history, ConcurrentHashMap.newKeySet());
        }

        sessions.get(history).add(session);
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {

        JudgeHistory history = (JudgeHistory) session.getAttributes().get(WebSocketCustomHeader.JUDGE);

        if (history == null) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        sessions.get(history).remove(session);
    }
}
