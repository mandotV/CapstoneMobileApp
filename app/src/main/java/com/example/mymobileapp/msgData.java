package com.example.mymobileapp;

public class msgData {

    String id;
    String sender;
    String receiver;
    String timestamp;
    String pp;
    String statusmobile;

    public msgData(){}

    public msgData(String id, String sender, String receiver, String timestamp, String pp, String statusmobile) {

        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.id = id;
        this.pp = pp;
        this.statusmobile = statusmobile;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getStatusmobile() {
        return statusmobile;
    }

    public void setStatusmobile(String statusmobile) {
        this.statusmobile = statusmobile;
    }
}
