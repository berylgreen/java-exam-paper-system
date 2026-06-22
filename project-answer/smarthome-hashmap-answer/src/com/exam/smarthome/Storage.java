package com.exam.smarthome;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Device> items = new HashMap<>();
    public void add(Device item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Device get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
