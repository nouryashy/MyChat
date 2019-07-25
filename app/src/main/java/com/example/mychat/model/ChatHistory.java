package com.example.mychat.model;

import android.util.Log;

public class ChatHistory {
    private String mLastMessage;
    private Long mLastMessageDate;

    public ChatHistory() {

    }

    public ChatHistory(String lastMessage, Long lastMessageDate) {
        mLastMessage = lastMessage;
        mLastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String lastMessage) {
        mLastMessage = lastMessage;
    }

    public Long getLastMessageDate() {
        return mLastMessageDate;
    }

    public void setLastMessageDate(Long lastMessageDate) {
        mLastMessageDate = lastMessageDate;
    }


}
