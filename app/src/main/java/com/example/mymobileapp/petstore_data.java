package com.example.mymobileapp;

public class petstore_data {

    String owner;
    String price;
    String servimglink;
    String date;
    String name;

    public petstore_data(){}

    public petstore_data(String owner, String price, String servimglink, String date, String name) {
        this.owner = owner;
        this.price = price;
        this.servimglink = servimglink;
        this.date = date;
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getServimglink() {
        return servimglink;
    }

    public void setServimglink(String servimglink) {
        this.servimglink = servimglink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
