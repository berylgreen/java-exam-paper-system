package com.exam.hotel;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        RoomProcessor[] processors = { new RegularRoom("总统套房"), new VIPRoom("豪华大床房") };
        for (RoomProcessor p : processors) { p.process(); }
    }
}
