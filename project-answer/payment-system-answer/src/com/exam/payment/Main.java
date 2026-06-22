package com.exam.payment;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        OrderProcessor[] processors = { new TypeA("订单1"), new TypeB("订单2") };
        for (OrderProcessor p : processors) { p.process(); }
    }
}
