package com.exam.library;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Book> map = new HashMap<>();
        map.put("101", new Book("101", "Java编程思想"));
        map.put("102", new Book("102", "算法导论"));
        map.put("103", new Book("103", "计算机网络"));
        System.out.println("添加后图书数量：" + map.size());
        Book item = map.get("102");
        System.out.println("查询 id=102 的图书：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
