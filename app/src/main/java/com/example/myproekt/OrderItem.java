    package com.example.myproekt;

    public class OrderItem {
        private int id;
        private long orderId;
        private Product product;
        private int quantity;

        public OrderItem(int id, long orderId, Product product, int quantity) {
            this.id = id;
            this.orderId = orderId;
            this.product = product;
            this.quantity = quantity;
        }

        // Геттеры
        public int getId() { return id; }
        public long getOrderId() { return orderId; }
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
    }