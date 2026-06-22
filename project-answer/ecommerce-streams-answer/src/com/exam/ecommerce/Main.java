package com.exam.ecommerce;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Order> map = new HashMap<>();
        map.put("101", new Order("101", "笔记本电脑"));
        map.put("102", new Order("102", "智能手机"));
        map.put("103", new Order("103", "蓝牙耳机"));
        System.out.println("添加后商品数量：" + map.size());
        Order item = map.get("102");
        System.out.println("查询 id=102 的商品：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
