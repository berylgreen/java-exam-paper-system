package com.exam.payment;
interface OrderProcessor { void process(); }
class TypeA implements OrderProcessor {
    private String name;
    public TypeA(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础订单：" + name); }
}
class TypeB implements OrderProcessor {
    private String name;
    public TypeB(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级订单：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        OrderProcessor[] processors = { new TypeA("订单1"), new TypeB("订单2") };
        for (OrderProcessor p : processors) { p.process(); }
    }
}
