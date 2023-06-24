package com.example.mymobileapp;

public class favedata {

    String bname;
    String email;
    String address;
    String petlover;
    String imglink;
    String username;
    String Type;


    public favedata(){}

    public favedata(String email, String bname, String address, String petlover, String username, String imglink
    ,String Type) {
        this.email = email;
        this.bname = bname;
        this.address = address;
        this.petlover = petlover;
        this.username = username;
        this.imglink = imglink;
        this.Type = Type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getPetlover() {
        return petlover;
    }

    public void setPetlover(String petlover) {
        this.petlover = petlover;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
