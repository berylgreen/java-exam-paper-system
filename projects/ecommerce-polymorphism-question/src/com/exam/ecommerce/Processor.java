package com.exam.ecommerce;

public class Processor {
    // 原始设计：使用大量 instanceof，违背开闭原则
    public void processAll(Object[] items) {
        for (Object obj : items) {
            if (obj instanceof StandardOrder) {
                ((StandardOrder) obj).doType1Logic();
            } else if (obj instanceof ExpressOrder) {
                ((ExpressOrder) obj).doType2Logic();
            }
        }
    }
}
