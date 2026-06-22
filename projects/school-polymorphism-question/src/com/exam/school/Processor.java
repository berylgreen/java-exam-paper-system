package com.exam.school;

public class Processor {
    // 原始设计：使用大量 instanceof，违背开闭原则
    public void processAll(Object[] items) {
        for (Object obj : items) {
            if (obj instanceof Undergraduate) {
                ((Undergraduate) obj).doType1Logic();
            } else if (obj instanceof Graduate) {
                ((Graduate) obj).doType2Logic();
            }
        }
    }
}
