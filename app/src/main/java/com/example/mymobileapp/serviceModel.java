package com.example.mymobileapp;

public class serviceModel {

    String Price;
    String Name;
    String Description;
    String servimglink;
    String prodimglink;
    String Stock;

    public serviceModel(){}

    public serviceModel(String price, String name, String servimglink, String description, String stock, String prodimglink ) {
        this.Price = price;
        this.Name = name;
        this.servimglink = servimglink;
        this.Description = description;
        this.Stock = stock;
        this.prodimglink = prodimglink;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getServimglink() {
        return servimglink;
    }

    public void setServimglink(String servimglink) {
        this.servimglink = servimglink;
    }

    public String getProdimglink() {
        return prodimglink;
    }

    public void setProdimglink(String prodimglink) {
        this.prodimglink = prodimglink;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }
}
