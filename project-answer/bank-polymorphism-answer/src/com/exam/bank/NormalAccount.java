package com.exam.bank;
public class NormalAccount implements AccountProcessor {
    private String name;
    public NormalAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础账户：" + name); }
}
