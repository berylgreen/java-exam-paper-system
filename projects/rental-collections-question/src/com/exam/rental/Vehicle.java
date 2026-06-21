package com.exam.rental;

public class Vehicle {
    private String id;
    
    public Vehicle(String id) {
        this.id = id;
    }
    
    // TODO: 1. 重写 equals() 和 hashCode() 以实现去重


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
