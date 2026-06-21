package com.exam.smarthome;

public class Device {
    private String id;
    private String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Device(String id, String type) {
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
