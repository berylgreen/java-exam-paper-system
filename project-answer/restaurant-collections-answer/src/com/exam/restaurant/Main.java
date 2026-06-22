package com.exam.restaurant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Dish> set = new HashSet<>();
        set.add(new Dish("103", "红烧肉"));
        set.add(new Dish("101", "宫保鸡丁"));
        set.add(new Dish("102", "鱼香肉丝"));
        set.add(new Dish("102", "鱼香肉丝")); // 重复对象
        System.out.println("添加后去重的菜品数量：" + set.size());
        List<Dish> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Dish item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
