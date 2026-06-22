package com.exam.payment;
public class TypeB implements OrderProcessor {
    private String name;
    public TypeB(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级订单：" + name); }
}
