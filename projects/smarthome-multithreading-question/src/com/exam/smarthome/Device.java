package com.exam.smarthome;

public class Device {
    // 原始设计：共享变量未做同步控制
    private int stock = 100;

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
