class BookException extends Exception {
    public BookException(String message) {
        super(message);
    }
}

public class BookParser {
    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseBook(item);
                System.out.println("解析成功: " + item);
            } catch (BookException e) {
                System.err.println("图书数据解析失败: " + e.getMessage());
            }
        }
    }

    private void parseBook(String item) throws BookException {
        if (item == null || item.trim().isEmpty()) {
            throw new BookException("图书数据为空或格式不正确");
        }
        // 这里可继续补充更具体的解析逻辑
    }
}