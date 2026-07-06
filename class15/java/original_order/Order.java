package com.tdd.tddmock.order;

public class Order {
    private String orderId;
    private String product;
    private int quantity;

    // Constructor, getters, and setters...

    Order(String orderId, String product, int quantity) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
    }
}

