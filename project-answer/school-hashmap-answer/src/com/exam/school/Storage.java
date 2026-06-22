package com.exam.school;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Student> items = new HashMap<>();
    public void add(Student item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Student get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
