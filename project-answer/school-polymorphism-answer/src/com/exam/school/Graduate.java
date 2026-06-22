package com.exam.school;
public class Graduate implements StudentProcessor {
    private String name;
    public Graduate(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级课程：" + name); }
}
