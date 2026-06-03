package com.exam.logistics;

public class Package {
    public String id;
    
    public Package(String id) {
        this.id = id;
    }
    
    // TODO: 1. 重写 equals() 和 hashCode() 以实现去重
    // TODO: 2. 实现 Comparable 接口以支持排序
}
