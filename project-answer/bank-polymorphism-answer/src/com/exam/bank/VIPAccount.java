package com.exam.bank;
public class VIPAccount implements AccountProcessor {
    private String name;
    public VIPAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级账户：" + name); }
}
