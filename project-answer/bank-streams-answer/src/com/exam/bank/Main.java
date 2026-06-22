package com.exam.bank;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Account> map = new HashMap<>();
        map.put("101", new Account("101", "张三的账户"));
        map.put("102", new Account("102", "李四的账户"));
        map.put("103", new Account("103", "王五的账户"));
        System.out.println("添加后账户数量：" + map.size());
        Account item = map.get("102");
        System.out.println("查询 id=102 的账户：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
