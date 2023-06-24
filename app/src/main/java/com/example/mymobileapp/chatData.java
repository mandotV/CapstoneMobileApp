package com.example.mymobileapp;

public class chatData {
    String timestamp;
    String id;
    String message;
    String sender;
    String receiver;


    public chatData(){}


    public chatData(String id,String message, String sender, String receiver, String timestamp) {
        this.timestamp = timestamp;
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
