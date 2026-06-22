package com.exam.rental;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Vehicle> map = new HashMap<>();
        map.put("101", new Vehicle("101", "丰田卡罗拉"));
        map.put("102", new Vehicle("102", "本田雅阁"));
        map.put("103", new Vehicle("103", "宝马X5"));
        System.out.println("添加后车辆数量：" + map.size());
        Vehicle item = map.get("102");
        System.out.println("查询 id=102 的车辆：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
