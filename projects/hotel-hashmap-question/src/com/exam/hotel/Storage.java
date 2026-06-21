package com.exam.hotel;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    // 原始设计：使用一个 List 存储对象，查询时需要遍历整个列表，效率低
    private List<Room> items = new ArrayList<>();
    
    public void add(String id, Room item) {
        items.add(item);
    }
    
    public Room get(String id) {
        for (Room item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
    
    public void remove(String id) {
        Room target = null;
        for (Room item : items) {
            if (item.getId().equals(id)) {
                target = item;
                break;
            }
        }
        if (target != null) {
            items.remove(target);
        }
    }
}
