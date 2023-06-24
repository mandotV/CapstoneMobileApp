package com.example.mymobileapp;

public class cartData {
    String name;
    String cartlink;
    String price;
    String quantity;
    String Type;

    public cartData(){}

    public cartData(String name, String cartlink, String price, String quantity, String Type) {
        this.name = name;
        this.cartlink = cartlink;
        this.price = price;
        this.quantity = quantity;
        this.Type = Type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCartlink() {
        return cartlink;
    }

    public void setCartlink(String cartlink) {
        this.cartlink = cartlink;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
