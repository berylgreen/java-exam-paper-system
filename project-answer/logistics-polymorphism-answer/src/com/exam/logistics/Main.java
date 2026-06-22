package com.exam.logistics;
interface PackageProcessor { void process(); }
class StandardPackage implements PackageProcessor {
    private String name;
    public StandardPackage(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础包裹：" + name); }
}
class FragilePackage implements PackageProcessor {
    private String name;
    public FragilePackage(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级包裹：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        PackageProcessor[] processors = { new StandardPackage("电子产品包裹"), new FragilePackage("书籍包裹") };
        for (PackageProcessor p : processors) { p.process(); }
    }
}
