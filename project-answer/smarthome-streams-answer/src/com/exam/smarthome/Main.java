package com.exam.smarthome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Device> set = new HashSet<>();
        set.add(new Device("103", "智能门锁"));
        set.add(new Device("101", "智能灯"));
        set.add(new Device("102", "智能空调"));
        set.add(new Device("102", "智能空调")); // 重复对象
        System.out.println("添加后去重的设备数量：" + set.size());
        List<Device> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Device item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
