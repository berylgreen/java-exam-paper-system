package com.exam.weather;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 weather 模块测试...");
        Storage storage = new Storage();
        storage.add(new WeatherData("101", "温度数据"));
        storage.add(new WeatherData("102", "湿度数据"));
        storage.add(new WeatherData("103", "气压数据"));
        
        WeatherData item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
