package com.exam.library;
interface BookProcessor { void process(); }
class Textbook implements BookProcessor {
    private String name;
    public Textbook(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础图书：" + name); }
}
class Magazine implements BookProcessor {
    private String name;
    public Magazine(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级图书：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        BookProcessor[] processors = { new Textbook("Java编程思想"), new Magazine("算法导论") };
        for (BookProcessor p : processors) { p.process(); }
    }
}
