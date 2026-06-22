package com.exam.rental;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Vehicle> items = new HashMap<>();
    public void add(Vehicle item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Vehicle get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
