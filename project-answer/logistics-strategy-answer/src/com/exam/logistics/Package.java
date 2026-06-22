package com.exam.logistics;

public class Package {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Package(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
