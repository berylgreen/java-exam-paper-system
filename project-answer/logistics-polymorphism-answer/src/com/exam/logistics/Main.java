package com.exam.logistics;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        PackageProcessor[] processors = { new StandardPackage("电子产品包裹"), new FragilePackage("书籍包裹") };
        for (PackageProcessor p : processors) { p.process(); }
    }
}
