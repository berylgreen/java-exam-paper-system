package com.exam.hospital;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Patient> map = new HashMap<>();
        map.put("101", new Patient("101", "张三的病历"));
        map.put("102", new Patient("102", "李四的病历"));
        map.put("103", new Patient("103", "王五的病历"));
        System.out.println("添加后病历数量：" + map.size());
        Patient item = map.get("102");
        System.out.println("查询 id=102 的病历：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
