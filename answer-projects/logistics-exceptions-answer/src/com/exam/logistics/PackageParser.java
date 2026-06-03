class PackageException extends Exception {
    public PackageException(String message) {
        super(message);
    }
}

public class PackageParser {

    // 解析单条包裹数据
    public void parsePackage(String data) throws PackageException {
        if (data == null || data.trim().isEmpty()) {
            throw new PackageException("包裹数据不能为空");
        }

        // 示例：约定包裹数据中必须包含“-”作为格式标记
        if (!data.contains("-")) {
            throw new PackageException("包裹数据格式不正确：" + data);
        }

        System.out.println("解析成功：" + data);
    }

    // 批量解析包裹数据
    public void parseList(String[] dataList) {
        for (String data : dataList) {
            try {
                parsePackage(data);
            } catch (PackageException e) {
                System.err.println("错误日志：" + e.getMessage());
                // 实际项目中还可以将异常信息写入日志文件或监控系统
            }
        }
    }
}