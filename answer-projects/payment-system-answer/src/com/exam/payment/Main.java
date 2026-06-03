package com.exam.payment;

public class Main {
    public static void main(String[] args) {
        Order order1 = new Order("ORD001", 100.0);
        PaymentProcessor processor = new PaymentProcessor();
        processor.processPayment(order1);
        
        // TODO: 根据 README.md 的要求重构代码并添加新的测试逻辑
    }
}
