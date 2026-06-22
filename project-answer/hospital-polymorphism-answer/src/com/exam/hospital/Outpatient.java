package com.exam.hospital;
public class Outpatient implements PatientProcessor {
    private String name;
    public Outpatient(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础病历：" + name); }
}
