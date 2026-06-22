package com.exam.hospital;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Patient> items = new HashMap<>();
    public void add(Patient item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Patient get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
