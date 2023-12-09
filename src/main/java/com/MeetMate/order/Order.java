package com.MeetMate.order;

import com.MeetMate.user.User;
import jakarta.persistence.Entity;

public class Order {

    private Long orderId;
    private User user;

    public Order() {
    }

    public Order(Long id, User user) {
        this.orderId = id;
        this.user = user;
    }

    public Order(User user) {
        this.user = user;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + orderId +
                ", user=" + user +
                '}';
    }
}
