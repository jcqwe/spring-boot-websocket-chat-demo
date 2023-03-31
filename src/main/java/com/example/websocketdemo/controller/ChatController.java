package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;    //Spring WebSocket消息发送模板

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chatToUser")
    public ChatMessage sendMessageToUser(@Payload ChatMessage chatMessage) {
        log.info("{}",chatMessage.getUser().toString());
        //向指定客户端发送消息，第一个参数消息接收用户，客户端订阅消息地址为：/user/${username}/queue/justL,消息内容
        messagingTemplate.convertAndSendToUser(chatMessage.getUser().getToUserName(), "/queue/justL", chatMessage);
        //发送给自己
        messagingTemplate.convertAndSendToUser(chatMessage.getUser().getSendUserName(), "/queue/chatToUser/res", chatMessage);
        return chatMessage;
    }
}
