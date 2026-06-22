package com.exam.payment;

public class WechatPay implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("订单 " + order.getOrderId() + " 使用微信支付了 " + order.getAmount() + " 元");
    }
}

// Alipay.java
