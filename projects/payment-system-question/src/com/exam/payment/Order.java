package com.exam.payment;

public class Order {
    private String orderId;
    private double amount;

    public Order(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
    
    public String getOrderId() {
        return orderId;
    }
}
