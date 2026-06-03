class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}

public class DataParser {
    public void parseScores(String[] data) {
        for (String s : data) {
            try {
                try {
                    if (s == null) {
                        throw new NullPointerException("数据为 null");
                    }
                    int score = Integer.parseInt(s);
                    System.out.println("Parsed: " + score);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("数据格式非法：" + s);
                } catch (NullPointerException e) {
                    throw new InvalidDataException("数据不能为空");
                }
            } catch (InvalidDataException e) {
                System.err.println("解析失败：" + e.getMessage());
            } finally {
                System.out.println("解析过程结束");
            }
        }
    }
}