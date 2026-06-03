package com.exam.weather;

public class DisplayPanel {
    public void renderData(float t, float h, float p) {
        System.out.println("大屏显示板更新 - 温度: " + t + " 湿度: " + h + " 气压: " + p);
    }
}
