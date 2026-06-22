package com.exam.ecommerce;

class OrderException extends Exception {
    public OrderException(String message) {
        super(message);
    }
}

public class OrderParser {

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parse(item);
            } catch (OrderException e) {
                System.err.println("订单解析失败：" + e.getMessage());
            }
        }
    }

    private void parse(String item) throws OrderException {
        if (item == null || item.trim().isEmpty()) {
            throw new OrderException("订单数据为空或格式不合法");
        }

        System.out.println("解析成功：" + item);
    }
}
