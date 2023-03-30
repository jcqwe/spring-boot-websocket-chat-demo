package com.example.websocketdemo.model;

import lombok.Data;
import lombok.ToString;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Data
@ToString
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private User user;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
