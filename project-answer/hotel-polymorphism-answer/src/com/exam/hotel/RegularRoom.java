package com.exam.hotel;
public class RegularRoom implements RoomProcessor {
    private String name;
    public RegularRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础客房：" + name); }
}
