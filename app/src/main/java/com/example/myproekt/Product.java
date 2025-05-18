package com.example.myproekt;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String details; // Новое поле

    public Product(int id, String name, String description, double price, int categoryId, String details) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.details = details;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getCategoryId() { return categoryId; }
    public String getDetails() { return details; } // Новый геттер

    public String getWeight() {
        return categoryId == 5 ? "1л" : "100г";
    }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setDetails(String details) { this.details = details; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}