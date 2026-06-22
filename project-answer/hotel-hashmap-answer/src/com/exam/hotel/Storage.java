package com.exam.hotel;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Room> items = new HashMap<>();
    public void add(Room item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Room get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
