class PatientException extends Exception {
    public PatientException(String message) {
        super(message);
    }
}

public class PatientParser {

    public void parsePatient(String data) throws PatientException {
        if (data == null || data.trim().isEmpty()) {
            throw new PatientException("病患数据为空，无法解析");
        }

        // 示例：简单模拟格式校验
        if (!data.contains(",")) {
            throw new PatientException("病患数据格式不合法：" + data);
        }

        System.out.println("解析成功：" + data);
    }

    public void parseList(String[] dataList) {
        for (String data : dataList) {
            try {
                parsePatient(data);
            } catch (PatientException e) {
                System.err.println("解析失败：" + e.getMessage());
                // 此处可扩展为写入日志文件或日志系统
            }
        }
    }
}