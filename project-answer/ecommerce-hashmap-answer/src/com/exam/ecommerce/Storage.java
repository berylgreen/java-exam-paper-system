package com.exam.ecommerce;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Order> items = new HashMap<>();
    public void add(Order item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Order get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
