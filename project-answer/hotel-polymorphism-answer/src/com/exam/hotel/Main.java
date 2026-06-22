package com.exam.hotel;
interface RoomProcessor { void process(); }
class RegularRoom implements RoomProcessor {
    private String name;
    public RegularRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础客房：" + name); }
}
class VIPRoom implements RoomProcessor {
    private String name;
    public VIPRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级客房：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        RoomProcessor[] processors = { new RegularRoom("总统套房"), new VIPRoom("豪华大床房") };
        for (RoomProcessor p : processors) { p.process(); }
    }
}
