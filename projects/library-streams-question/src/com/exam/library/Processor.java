package com.exam.library;

import java.util.ArrayList;
import java.util.List;

public class Processor {
    // 原始设计：使用大量 for 循环和嵌套 if 语句进行筛选和提取
    public List<String> processList(List<Book> list) {
        List<String> result = new ArrayList<>();
        for (Book item : list) {
            if (item.valid == true) {
                result.add(item.name);
            }
        }
        return result;
    }
}
