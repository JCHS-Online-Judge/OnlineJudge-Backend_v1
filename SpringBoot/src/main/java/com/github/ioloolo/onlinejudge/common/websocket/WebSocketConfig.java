package com.github.ioloolo.onlinejudge.common.websocket;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.github.ioloolo.onlinejudge.domain.judge.websocket.AllHandler;
import com.github.ioloolo.onlinejudge.domain.judge.websocket.EachHandler;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

	private final EachHandler eachHandler;
	private final AllHandler  allHandler;

	private final LoggingInterceptor loggingInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(eachHandler, "/api/ws/each")
				.addHandler(allHandler, "/api/ws/all")
				.addInterceptors(loggingInterceptor);
	}

	@Slf4j
	@Component
	protected static class LoggingInterceptor implements HandshakeInterceptor {

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
}
