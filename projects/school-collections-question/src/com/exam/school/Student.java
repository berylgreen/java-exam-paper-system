package com.exam.school;

public class Student {
    private String id;
    
    public Student(String id) {
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
