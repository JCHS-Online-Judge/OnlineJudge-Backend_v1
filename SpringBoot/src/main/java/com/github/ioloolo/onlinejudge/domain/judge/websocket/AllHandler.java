package com.github.ioloolo.onlinejudge.domain.judge.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
public class AllHandler extends TextWebSocketHandler {

	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

	@Override
	public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(
			@Nonnull WebSocketSession session, @Nonnull CloseStatus status
	) {

		sessions.remove(session);
	}
}
