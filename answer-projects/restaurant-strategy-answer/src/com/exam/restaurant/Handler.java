package com.exam.restaurant;

public class Handler {
    // 原始设计：使用 switch-case 区分不同处理策略，扩展性差
    public void handle(Dish item) {
        switch (item.type) {
            case "TYPE_A":
                System.out.println("使用策略A处理: " + item.id);
                break;
            case "TYPE_B":
                System.out.println("使用策略B处理: " + item.id);
                break;
            default:
                System.out.println("未知策略");
        }
    }
}
