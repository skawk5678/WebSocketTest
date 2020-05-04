package yoonsung.band.sns.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class SocketTextHandler extends TextWebSocketHandler {
	HashMap<String, WebSocketSession> sessions = new HashMap<>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		String payload = message.getPayload();
		log.info("message: {}, remoteAddress: {}", payload, session.getRemoteAddress());
		
		try {
			// 접속된 모든 세션에 메시지 전송
			for (String key : sessions.keySet()) {
				WebSocketSession ss = sessions.get(key);
				ss.sendMessage(new TextMessage(payload));
			}
		} catch (IOException e) {
			log.error("[handleTextMessage] error: {}", e);
		}
	}

	// 세션이 생성될때 시작되는 함수
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		sessions.put(session.getId(), session);
	}

	// 세션이 끝날때 실행되는 함수
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session.getId());
		super.afterConnectionClosed(session, status);
	}
}
