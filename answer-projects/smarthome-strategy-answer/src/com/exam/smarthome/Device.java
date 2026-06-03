package com.exam.smarthome;

public class Device {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Device(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
