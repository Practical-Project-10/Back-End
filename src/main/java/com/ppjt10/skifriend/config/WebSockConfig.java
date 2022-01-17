package com.ppjt10.skifriend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker   //웹 소켓 관련 설정 작동
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메모리 기반 메시지 브로커가 해당 api 구독하는 클라에게 메시지 전달
        config.enableSimpleBroker("/sub");

        // 서버에서 클라이언트로부터의 메시지를 받을 api 의 prefix
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 여러가지 Endpoint 설정
        registry.addEndpoint("/ws-stomp", "/ws-alarm")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // StompHandler 가 Websocket 앞단에서 token 을 체크할 수 있도록 인터셉터로 설정
        registration.interceptors(stompHandler);
    }
}
