package com.exam.hospital;
public class Emergency implements PatientProcessor {
    private String name;
    public Emergency(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级病历：" + name); }
}
