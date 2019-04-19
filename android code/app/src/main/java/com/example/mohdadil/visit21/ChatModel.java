package com.example.mohdadil.visit21;

/**
 * Created by Gloria on 09-03-2019.
 */

public class ChatModel {
    public String ChatMessage;
    public boolean isSend;

    public ChatModel(String chatMessage, boolean isSend) {
        ChatMessage = chatMessage;
        this.isSend = isSend;
    }

    public String getChatMessage() {
        return ChatMessage;
    }

    public void setChatMessage(String chatMessage) {
        ChatMessage = chatMessage;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}

