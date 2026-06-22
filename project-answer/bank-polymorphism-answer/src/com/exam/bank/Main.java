package com.exam.bank;
interface AccountProcessor { void process(); }
class NormalAccount implements AccountProcessor {
    private String name;
    public NormalAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础账户：" + name); }
}
class VIPAccount implements AccountProcessor {
    private String name;
    public VIPAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级账户：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        AccountProcessor[] processors = { new NormalAccount("张三的账户"), new VIPAccount("李四的账户") };
        for (AccountProcessor p : processors) { p.process(); }
    }
}
