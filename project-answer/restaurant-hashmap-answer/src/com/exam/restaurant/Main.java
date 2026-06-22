package com.exam.restaurant;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Dish> map = new HashMap<>();
        map.put("101", new Dish("101", "宫保鸡丁"));
        map.put("102", new Dish("102", "鱼香肉丝"));
        map.put("103", new Dish("103", "红烧肉"));
        System.out.println("添加后菜品数量：" + map.size());
        Dish item = map.get("102");
        System.out.println("查询 id=102 的菜品：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
