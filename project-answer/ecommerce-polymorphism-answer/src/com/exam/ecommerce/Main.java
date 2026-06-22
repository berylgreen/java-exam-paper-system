package com.exam.ecommerce;
interface OrderProcessor { void process(); }
class StandardOrder implements OrderProcessor {
    private String name;
    public StandardOrder(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础商品：" + name); }
}
class ExpressOrder implements OrderProcessor {
    private String name;
    public ExpressOrder(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级商品：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        OrderProcessor[] processors = { new StandardOrder("笔记本电脑"), new ExpressOrder("智能手机") };
        for (OrderProcessor p : processors) { p.process(); }
    }
}
