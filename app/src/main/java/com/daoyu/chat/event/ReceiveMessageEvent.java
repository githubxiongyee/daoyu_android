package com.daoyu.chat.event;

public class ReceiveMessageEvent  {
    public String userId;

    public ReceiveMessageEvent(String userId) {
        this.userId = userId;
    }

}
