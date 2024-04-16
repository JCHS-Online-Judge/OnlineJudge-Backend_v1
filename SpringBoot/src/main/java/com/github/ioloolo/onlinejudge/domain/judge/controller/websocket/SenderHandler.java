package com.github.ioloolo.onlinejudge.domain.judge.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.header.WebSocketCustomHeader;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SenderHandler extends TextWebSocketHandler {

    private final JudgeRepository judgeRepository;

    private final Set<WebSocketSession> common = ConcurrentHashMap.newKeySet();
    private final Set<WebSocketSession> contest = ConcurrentHashMap.newKeySet();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

        HttpHeaders header = session.getHandshakeHeaders();

        String judgeId = OptionalParser.parse(Optional.ofNullable(header.getFirst(WebSocketCustomHeader.JUDGE_ID)), ExceptionFactory.Type.INVALID_HEADER);

        JudgeHistory history = OptionalParser.parse(judgeRepository.findById(judgeId), ExceptionFactory.Type.HISTORY_NOT_FOUND);

        Map<String, Object> attributes = session.getAttributes();
        attributes.put(WebSocketCustomHeader.JUDGE, history);

        (history.getProblem().isCommon() ? common : contest).add(session);
    }

    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session, @Nonnull TextMessage message) throws Exception {

        Map<String, Object> attributes = session.getAttributes();
        JudgeHistory history = (JudgeHistory) attributes.get(WebSocketCustomHeader.JUDGE);

        if (history == null) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        sendAll(SubjectEachHandler.getSessions().get(history), history);

        if (history.getProblem().isCommon()) {
            sendAll(SubjectAllHandler.getSessions(), history);
        }
    }


    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {

        JudgeHistory history = (JudgeHistory) session.getAttributes().get(WebSocketCustomHeader.JUDGE);

        if (history == null) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        (history.getProblem().isCommon() ? common : contest).remove(session);
    }

    private void send(WebSocketSession session, JudgeHistory history) throws IOException {

        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(history)));
        }
    }

    private void sendAll(Collection<WebSocketSession> sessions, JudgeHistory history) throws IOException {

        for (WebSocketSession session : sessions) {
            send(session, history);
        }
    }
}
