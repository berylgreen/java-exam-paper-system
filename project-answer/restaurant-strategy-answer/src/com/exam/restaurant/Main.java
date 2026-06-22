package com.exam.restaurant;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("宫保鸡丁");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("鱼香肉丝");
    }
}
