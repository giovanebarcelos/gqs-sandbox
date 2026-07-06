package com.tdd.tddmock.order;

import java.util.List;

public interface OrderService {
    void placeOrder(Order order);
    List<Order> getAllOrders();
}
