package com.exam.payment;

public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();

        Order order1 = new Order("ORD002", 200.0);
        processor.setPaymentStrategy(new WechatPay());
        processor.processPayment(order1);

        Order order2 = new Order("ORD003", 300.0);
        processor.setPaymentStrategy(new Alipay());
        processor.processPayment(order2);
    }
}
