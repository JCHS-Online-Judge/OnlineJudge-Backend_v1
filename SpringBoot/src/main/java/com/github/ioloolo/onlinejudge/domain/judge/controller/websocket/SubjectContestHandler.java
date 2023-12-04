package com.github.ioloolo.onlinejudge.domain.judge.controller.websocket;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.repository.ContestRepository;
import com.github.ioloolo.onlinejudge.domain.judge.controller.websocket.header.WebSocketCustomHeader;
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
public class SubjectContestHandler extends TextWebSocketHandler {

    private final ContestRepository contestRepository;

    @Getter
    private static final Map<Contest, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

        HttpHeaders header = session.getHandshakeHeaders();

        String contestId = OptionalParser.parse(Optional.ofNullable(header.getFirst(WebSocketCustomHeader.CONTEST_ID)), ExceptionFactory.Type.INVALID_HEADER);

        Contest contest = OptionalParser.parse(contestRepository.findById(contestId), ExceptionFactory.Type.CONTEST_NOT_FOUND);

        session.getAttributes().put(WebSocketCustomHeader.CONTEST, contest);

        if (!sessions.containsKey(contest)) {
            sessions.put(contest, ConcurrentHashMap.newKeySet());
        }

        sessions.get(contest).add(session);
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {

        Contest contest = (Contest) session.getAttributes().get(WebSocketCustomHeader.CONTEST);

        if (contest == null) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        sessions.get(contest).remove(session);
    }
}
