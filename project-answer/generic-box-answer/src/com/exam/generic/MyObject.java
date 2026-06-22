package com.exam.generic;
import java.util.Objects;
public class MyObject implements Comparable<MyObject> {
    private String id;
    private String name;
    private double value;
    public MyObject() {}
    public MyObject(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("编号不能为空");
        this.id = id;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getValue() { return value; }
    public void setValue(double value) {
        if (value < 0) throw new IllegalArgumentException("数值不能为负数");
        this.value = value;
    }
    @Override
    public boolean equals(MyObject o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyObject that = (MyObject) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(MyObject other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "MyObject{id='" + id + "', name='" + name + "'}"; }
}
