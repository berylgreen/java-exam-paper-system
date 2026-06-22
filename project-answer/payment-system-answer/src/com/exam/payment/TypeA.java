package com.exam.payment;
public class TypeA implements OrderProcessor {
    private String name;
    public TypeA(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础订单：" + name); }
}
