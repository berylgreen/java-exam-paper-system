package com.exam.rental;

class VehicleException extends Exception {
    public VehicleException(String message) {
        super(message);
    }
}

public class VehicleParser {

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseVehicle(item);
                System.out.println("Parsed: " + item);
            } catch (VehicleException e) {
                System.err.println("车辆数据解析失败：" + e.getMessage());
                // 此处可进一步写入日志文件或监控系统
            }
        }
    }

    private void parseVehicle(String item) throws VehicleException {
        if (item == null || item.trim().isEmpty()) {
            throw new VehicleException("数据为空或格式非法");
        }

        // 这里可以补充更具体的解析逻辑
        // 例如按约定格式拆分字段、校验车辆编号、品牌、租金等
    }
}
