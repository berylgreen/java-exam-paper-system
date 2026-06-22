package com.exam.rental;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Vehicle> set = new HashSet<>();
        set.add(new Vehicle("103", "宝马X5"));
        set.add(new Vehicle("101", "丰田卡罗拉"));
        set.add(new Vehicle("102", "本田雅阁"));
        set.add(new Vehicle("102", "本田雅阁")); // 重复对象
        System.out.println("添加后去重的车辆数量：" + set.size());
        List<Vehicle> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Vehicle item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
