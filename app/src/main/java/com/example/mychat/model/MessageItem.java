package com.example.mychat.model;

public class MessageItem {
    private String mMessage;
    private String mSenderId;
    private Long mMessageDate;

    public MessageItem() {
    }

    public MessageItem(String message, String senderId, Long messageDate) {
        mMessage = message;
        mSenderId = senderId;
        mMessageDate = messageDate;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public void setSenderId(String senderId) {
        mSenderId = senderId;
    }

    public Long getMessageDate() {
        return mMessageDate;
    }

    public void setMessageDate(Long messageDate) {
        mMessageDate = messageDate;
    }


}
