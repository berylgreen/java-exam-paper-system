package com.exam.school;
public class Undergraduate implements StudentProcessor {
    private String name;
    public Undergraduate(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础课程：" + name); }
}
