package com.exam.logistics;

public class Processor {
    // 原始设计：使用大量 instanceof，违背开闭原则
    public void processAll(Object[] items) {
        for (Object obj : items) {
            if (obj instanceof StandardPackage) {
                ((StandardPackage) obj).doType1Logic();
            } else if (obj instanceof FragilePackage) {
                ((FragilePackage) obj).doType2Logic();
            }
        }
    }
}
