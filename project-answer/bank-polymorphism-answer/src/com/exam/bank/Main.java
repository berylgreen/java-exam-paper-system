package com.exam.bank;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        AccountProcessor[] processors = { new NormalAccount("张三的账户"), new VIPAccount("李四的账户") };
        for (AccountProcessor p : processors) { p.process(); }
    }
}
