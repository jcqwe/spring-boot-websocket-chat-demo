package com.example.websocketdemo.model;

import lombok.Data;
import lombok.ToString;

import java.security.Principal;

/***
 *@title User
 *@description 发送userName,接收的userName
 *@author chc
 *@version 1.0.0
 *@create 2023/3/30 10:24
 **/
@Data
@ToString
public class User implements Principal {
    private String sendUserName;
    private String toUserName;

    @Override
    public String getName() {
        return this.toUserName;
    }
}
