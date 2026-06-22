package com.exam.ecommerce;
public class ExpressOrder implements OrderProcessor {
    private String name;
    public ExpressOrder(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级商品：" + name); }
}
