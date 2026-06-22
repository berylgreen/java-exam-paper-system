package com.exam.hotel;
public class VIPRoom implements RoomProcessor {
    private String name;
    public VIPRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级客房：" + name); }
}
