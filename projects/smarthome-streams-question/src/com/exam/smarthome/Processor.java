package com.exam.smarthome;

import java.util.ArrayList;
import java.util.List;

public class Processor {
    // 原始设计：使用大量 for 循环和嵌套 if 语句进行筛选和提取
    public List<String> processList(List<Device> list) {
        List<String> result = new ArrayList<>();
        for (Device item : list) {
            if (item.valid == true) {
                result.add(item.name);
            }
        }
        return result;
    }
}
