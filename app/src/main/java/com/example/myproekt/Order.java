package com.example.myproekt;

public class Order {
    private long id;
    private int userId;
    private double totalPrice;
    private String status;
    private String createdAt;

    public Order(long id, int userId, double totalPrice, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Геттеры
    public long getId() { return id; }
    public int getUserId() { return userId; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}