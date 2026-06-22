package com.exam.payment;

public class Alipay implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("订单 " + order.getOrderId() + " 使用支付宝支付了 " + order.getAmount() + " 元");
    }
}

// PaymentProcessor.java
