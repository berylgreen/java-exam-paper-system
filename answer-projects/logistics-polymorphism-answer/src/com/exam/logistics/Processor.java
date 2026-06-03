package com.exam.logistics;

public class Processor {
    // 原始设计：使用大量 if-else 和 instanceof 进行类型判断
    public void processAll(Package[] items) {
        for (Package obj : items) {
            if (obj instanceof RegularPackage) {
                System.out.println("处理普通业务逻辑");
            } else if (obj instanceof VIPPackage) {
                System.out.println("处理VIP业务逻辑");
            } else {
                System.out.println("处理未知业务逻辑");
            }
        }
    }
}
