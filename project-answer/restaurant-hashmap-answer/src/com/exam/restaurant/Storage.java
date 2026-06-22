package com.exam.restaurant;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Dish> items = new HashMap<>();
    public void add(Dish item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Dish get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
