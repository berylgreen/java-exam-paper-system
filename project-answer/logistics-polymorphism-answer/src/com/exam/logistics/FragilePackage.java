package com.exam.logistics;
public class FragilePackage implements PackageProcessor {
    private String name;
    public FragilePackage(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级包裹：" + name); }
}
