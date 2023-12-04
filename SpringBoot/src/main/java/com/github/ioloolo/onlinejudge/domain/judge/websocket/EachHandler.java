package com.github.ioloolo.onlinejudge.domain.judge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.repository.JudgeRepository;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class EachHandler extends TextWebSocketHandler {

    private final AllHandler allHandler;

    private final JudgeRepository repository;

    private final ObjectMapper objectMapper;

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    private final String CLIENT_TYPE_HEADER = "OJ-Client-Type";
    private final String JUDGE_ID_HEADER = "OJ-Judge-Id";

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {

        HttpHeaders header = session.getHandshakeHeaders();

        List<String> judgeIds = header.get(JUDGE_ID_HEADER);
        if (judgeIds == null || judgeIds.isEmpty()) {
            session.sendMessage(new TextMessage("Error: No judge id provided"));
            session.close();
            return;
        }
        String judgeId = judgeIds.get(0);

        List<String> types = header.get(CLIENT_TYPE_HEADER);
        if (types == null || types.isEmpty()) {
            session.sendMessage(new TextMessage("Error: No client type provided"));
            session.close();
            return;
        }
        String type = types.get(0);

        Map<String, Object> attributes = session.getAttributes();
        attributes.put(CLIENT_TYPE_HEADER, type);
        attributes.put(JUDGE_ID_HEADER, judgeId);

        sendInfo(session);

        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session, @Nonnull TextMessage message) {

        if (!session.getAttributes().get(CLIENT_TYPE_HEADER).equals("sender")) {
            return;
        }

        sessions.stream()
                .filter(WebSocketSession::isOpen)
                .filter(s -> s.getAttributes()
                        .get(JUDGE_ID_HEADER)
                        .equals(session.getAttributes().get(JUDGE_ID_HEADER)))
                .filter(s -> s.getAttributes().get(CLIENT_TYPE_HEADER).equals("subject"))
                .forEach(s -> {
                    try {
                        sendInfo(s);
                    } catch (Exception ignored) {
                    }
                });

        allHandler.getSessions().stream().filter(WebSocketSession::isOpen).forEach(s -> {
            try {
                sendInfo(s, session.getAttributes().get(JUDGE_ID_HEADER).toString());
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public void afterConnectionClosed(
            @Nonnull WebSocketSession session, @Nonnull CloseStatus status
    ) {

        sessions.remove(session);
    }

    private void sendInfo(WebSocketSession session) throws Exception {

        if ("sender".equals(session.getAttributes().get(CLIENT_TYPE_HEADER))) {
            return;
        }

        String judgeId = (String) session.getAttributes().get(JUDGE_ID_HEADER);

        sendInfo(session, judgeId);
    }

    private void sendInfo(WebSocketSession session, String judgeId) throws Exception {

        if ("sender".equals(session.getAttributes().get(CLIENT_TYPE_HEADER))) {
            return;
        }

        Optional<JudgeHistory> judgeHistoryOptional = repository.findById(judgeId);
        if (judgeHistoryOptional.isEmpty()) {
            return;
        }
        JudgeHistory judgeHistory = judgeHistoryOptional.get();

        judgeHistory.setProblem(Problem.builder()
                .id(judgeHistory.getProblem().getId())
                .problemNumber(judgeHistory.getProblem().getProblemNumber())
                .title(judgeHistory.getProblem().getTitle())
                .build());

        judgeHistory.setSourceCode(null);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(judgeHistory)));
    }
}
