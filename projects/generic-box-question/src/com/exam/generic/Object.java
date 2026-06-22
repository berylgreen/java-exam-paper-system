package com.exam.generic;

public class Object {
    public String id;
    public String name;
    public double value;
    
    public Object() {}
    
    public Object(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Object{id='" + id + "', name='" + name + "'}";
    }
}
