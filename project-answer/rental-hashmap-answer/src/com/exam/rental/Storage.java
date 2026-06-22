package com.exam.rental;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    // 原始设计：使用两个平行 List 存储 ID 和对象，查询效率低
    private List<String> ids = new ArrayList<>();
    private List<Vehicle> items = new ArrayList<>();
    
    public void add(String id, Vehicle item) {
        ids.add(id);
        items.add(item);
    }
    
    public Vehicle get(String id) {
        int index = ids.indexOf(id);
        if (index != -1) {
            return items.get(index);
        }
        return null;
    }
    
    public void remove(String id) {
        int index = ids.indexOf(id);
        if (index != -1) {
            ids.remove(index);
            items.remove(index);
        }
    }
}
