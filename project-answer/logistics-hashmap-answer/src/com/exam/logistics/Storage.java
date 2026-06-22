package com.exam.logistics;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Package> items = new HashMap<>();
    public void add(Package item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Package get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
