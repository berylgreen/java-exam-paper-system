package com.exam.payment;

public class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(Order order) {
        if (paymentStrategy == null) {
            System.out.println("未设置支付方式！");
            return;
        }
        paymentStrategy.pay(order);
    }
}

// Main.java
