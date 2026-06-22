package com.exam.rental;

public class Processor {
    // 原始设计：使用大量 if-else 和 instanceof 进行类型判断
    public void processAll(Vehicle[] items) {
        for (Vehicle obj : items) {
            if (obj instanceof RegularVehicle) {
                System.out.println("处理普通业务逻辑");
            } else if (obj instanceof VIPVehicle) {
                System.out.println("处理VIP业务逻辑");
            } else {
                System.out.println("处理未知业务逻辑");
            }
        }
    }
}
