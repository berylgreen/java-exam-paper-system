package com.exam.smarthome;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Device> map = new HashMap<>();
        map.put("101", new Device("101", "智能灯"));
        map.put("102", new Device("102", "智能空调"));
        map.put("103", new Device("103", "智能门锁"));
        System.out.println("添加后设备数量：" + map.size());
        Device item = map.get("102");
        System.out.println("查询 id=102 的设备：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
