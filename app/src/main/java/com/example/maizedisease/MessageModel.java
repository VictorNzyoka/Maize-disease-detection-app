package com.example.maizedisease;

public class MessageModel {
    private String msgid;
    private String senderId;
    private String message;

    public MessageModel(String msgid, String senderId, String message) {
        this.msgid = msgid;
        this.senderId = senderId;
        this.message = message;
    }

    public MessageModel() {
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
