package com.exam.logistics;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Package> map = new HashMap<>();
        map.put("101", new Package("101", "电子产品包裹"));
        map.put("102", new Package("102", "书籍包裹"));
        map.put("103", new Package("103", "衣物包裹"));
        System.out.println("添加后包裹数量：" + map.size());
        Package item = map.get("102");
        System.out.println("查询 id=102 的包裹：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
