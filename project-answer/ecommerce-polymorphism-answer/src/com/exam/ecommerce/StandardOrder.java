package com.exam.ecommerce;
public class StandardOrder implements OrderProcessor {
    private String name;
    public StandardOrder(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础商品：" + name); }
}
