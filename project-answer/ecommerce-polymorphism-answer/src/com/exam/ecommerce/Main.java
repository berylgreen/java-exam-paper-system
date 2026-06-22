package com.exam.ecommerce;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        OrderProcessor[] processors = { new StandardOrder("笔记本电脑"), new ExpressOrder("智能手机") };
        for (OrderProcessor p : processors) { p.process(); }
    }
}
