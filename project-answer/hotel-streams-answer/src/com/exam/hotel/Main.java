package com.exam.hotel;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Room> map = new HashMap<>();
        map.put("101", new Room("101", "总统套房"));
        map.put("102", new Room("102", "豪华大床房"));
        map.put("103", new Room("103", "标准间"));
        System.out.println("添加后客房数量：" + map.size());
        Room item = map.get("102");
        System.out.println("查询 id=102 的客房：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
