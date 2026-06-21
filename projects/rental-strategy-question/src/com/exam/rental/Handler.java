package com.exam.rental;

public class Handler {
    // 原始设计：使用 switch-case 区分不同处理策略，扩展性差
    public void handle(Vehicle item) {
        switch (item.getType()) {
            case "TYPE_A":
                System.out.println("使用策略A处理: " + item.getId());
                break;
            case "TYPE_B":
                System.out.println("使用策略B处理: " + item.getId());
                break;
            default:
                System.out.println("未知策略");
        }
    }
}
