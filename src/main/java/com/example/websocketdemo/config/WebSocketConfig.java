package com.example.websocketdemo.config;

import com.example.websocketdemo.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .addInterceptors(myHandshakeInterceptor())  //添加 websocket握手拦截器
//                .setHandshakeHandler(myDefaultHandshakeHandler())   //添加 websocket握手处理器
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //客户端向服务器发送消息地址的前缀
        registry.setApplicationDestinationPrefixes("/app");
        //设置客户端接收点对点消息地址的前缀，默认为 /user
        registry.setUserDestinationPrefix("/user");
        //客户端接收消息地址的前缀
        registry.enableSimpleBroker(
    "/topic",//群发消息前缀
                       "/queue",//发送对应用户消息前缀
                       "/user"
        );


        //   Use this for enabling a Full featured broker like RabbitMQ

        /*
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }

    /**
     * WebSocket 握手拦截器
     * 可做一些用户认证拦截处理
     */
    private HandshakeInterceptor myHandshakeInterceptor(){
        return new HandshakeInterceptor() {
            /**
             * websocket握手连接
             * @return 返回是否同意握手
             */
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    //WebSocket 握手处理器
    private DefaultHandshakeHandler myDefaultHandshakeHandler(){
        return new DefaultHandshakeHandler(){
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                //设置认证通过的用户到当前会话中
                return null;
            }
        };
    }

}
