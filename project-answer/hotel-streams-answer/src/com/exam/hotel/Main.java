package com.exam.hotel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Room> set = new HashSet<>();
        set.add(new Room("103", "标准间"));
        set.add(new Room("101", "总统套房"));
        set.add(new Room("102", "豪华大床房"));
        set.add(new Room("102", "豪华大床房")); // 重复对象
        System.out.println("添加后去重的客房数量：" + set.size());
        List<Room> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Room item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
