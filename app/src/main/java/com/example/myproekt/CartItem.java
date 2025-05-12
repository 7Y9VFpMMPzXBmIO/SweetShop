package com.example.myproekt;

public class CartItem {
    private int id; // ID записи в корзине
    private Product product;
    private int quantity;

    public CartItem(int id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    // Геттеры
    public int getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    // Сеттер для количества
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * @return Общая стоимость товара (цена * количество)
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}