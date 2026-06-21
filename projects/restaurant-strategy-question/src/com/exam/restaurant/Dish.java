package com.exam.restaurant;

public class Dish {
    private String id;
    private String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Dish(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
