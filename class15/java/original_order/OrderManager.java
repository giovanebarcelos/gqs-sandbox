package com.tdd.tddmock.order;

import java.util.List;

public class OrderManager {
    private OrderService orderService;

    public OrderManager(OrderService orderService) {
        this.orderService = orderService;
    }

    public void placeOrder(Order order) {
        orderService.placeOrder(order);
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
