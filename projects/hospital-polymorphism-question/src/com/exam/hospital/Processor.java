package com.exam.hospital;

public class Processor {
    // 原始设计：使用大量 instanceof，违背开闭原则
    public void processAll(Object[] items) {
        for (Object obj : items) {
            if (obj instanceof Outpatient) {
                ((Outpatient) obj).doType1Logic();
            } else if (obj instanceof Emergency) {
                ((Emergency) obj).doType2Logic();
            }
        }
    }
}
