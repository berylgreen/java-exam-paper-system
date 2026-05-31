package com.exam.payment;

public class PaymentProcessor {
    public void processPayment(Order order) {
        // 当前硬编码为现金支付
        System.out.println("订单 " + order.getOrderId() + " 使用现金支付了 " + order.getAmount() + " 元");
    }
}
