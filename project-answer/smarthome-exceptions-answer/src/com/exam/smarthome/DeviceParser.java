package com.exam.smarthome;

class DeviceException extends Exception {
    public DeviceException(String message) {
        super(message);
    }
}

public class DeviceParser {

    public void parseList(String[] dataList) {
        for (String data : dataList) {
            try {
                parseDevice(data);
                System.out.println("解析成功：" + data);
            } catch (DeviceException e) {
                System.err.println("设备数据解析失败：" + e.getMessage());
                // 这里可以进一步记录日志，例如写入文件或日志系统
            }
        }
    }

    private void parseDevice(String data) throws DeviceException {
        if (data == null || data.trim().isEmpty()) {
            throw new DeviceException("设备数据为空或格式非法");
        }

        // 模拟更严格的格式校验
        if (!data.contains(":")) {
            throw new DeviceException("设备数据缺少必要分隔符: " + data);
        }
    }

    public static void main(String[] args) {
        String[] devices = {
            "light:ON",
            "",
            "airConditioner-OFF",
            null,
            "door:OPEN"
        };

        DeviceParser parser = new DeviceParser();
        parser.parseList(devices);
    }
}