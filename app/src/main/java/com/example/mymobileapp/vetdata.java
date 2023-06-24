package com.example.mymobileapp;


public class vetdata{

    String Price;
    String Category;
    String petimglink;
    String Name;

    public vetdata(){}

    public vetdata(String price, String category, String petimglink, String Name) {
        this.Price = price;
        this.Category = category;
        this.petimglink = petimglink;
        this.Name = Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPetimglink() {
        return petimglink;
    }

    public void setPetimglink(String petimglink) {
        this.petimglink = petimglink;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
