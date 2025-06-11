package com.example.tint;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//WebSocket + STOMP 기반의 메시징 기능을 설정하는 구성 클래스
@Configuration   //Spring 설정 클래스
@EnableWebSocketMessageBroker  //STOMP 프로토콜을 사용하는 WebSocket 메시징을 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //WebSocketMessageBrokerConfigurer  WebSocket 메시징 기능을 사용자 정의 설정할 수 있도록 해주는 인터페이스.

    //클라이언트가 WebSocket 연결을 시작할 엔드포인트 URL을 정의한다
    //즉, JS에서 /ws로 접속하게 된다
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
//        withSockJS() : 웹 브라우저에서 WebSocket이 지원되지 않을 경우를 대비해 SockJS 폴백(fallback)을 지원한다.
//        자동으로 HTTP long-polling 방식으로 대체되므로 브라우저 호환성이 좋아진다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //서버 → 클라이언트로 보내는 메시지를 처리할 내장 메시지 브로커를 활성화.
        registry.enableSimpleBroker("/topic");
        //클라이언트 → 서버로 보낼 때 사용할 경로 prefix이다.
        registry.setApplicationDestinationPrefixes("/app");
    }
}
