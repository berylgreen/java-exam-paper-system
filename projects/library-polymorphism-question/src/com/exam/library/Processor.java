package com.exam.library;

public class Processor {
    // 原始设计：使用大量 instanceof，违背开闭原则
    public void processAll(Object[] items) {
        for (Object obj : items) {
            if (obj instanceof Textbook) {
                ((Textbook) obj).doType1Logic();
            } else if (obj instanceof Magazine) {
                ((Magazine) obj).doType2Logic();
            }
        }
    }
}
