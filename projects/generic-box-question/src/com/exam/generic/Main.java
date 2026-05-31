package com.exam.generic;

public class Main {
    public static void main(String[] args) {
        // FIXME: 这里使用了冗余的具体类型类，请改为统一泛型类 Box<T>
        StringBox strBox = new StringBox();
        strBox.set("Hello Generics");
        System.out.println(strBox.get());
        
        IntegerBox intBox = new IntegerBox();
        intBox.set(100);
        System.out.println(intBox.get());
    }
}
