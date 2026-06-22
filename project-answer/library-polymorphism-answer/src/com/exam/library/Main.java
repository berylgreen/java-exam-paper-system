package com.exam.library;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        BookProcessor[] processors = { new Textbook("Java编程思想"), new Magazine("算法导论") };
        for (BookProcessor p : processors) { p.process(); }
    }
}
