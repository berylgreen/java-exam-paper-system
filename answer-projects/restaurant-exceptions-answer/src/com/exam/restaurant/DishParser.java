class DishException extends Exception {
    public DishException(String message) {
        super(message);
    }
}

public class DishParser {

    public void parseDish(String item) throws DishException {
        if (item == null || item.trim().isEmpty()) {
            throw new DishException("菜品数据为空");
        }

        // 示例：约定菜品数据格式为“菜名:价格”
        if (!item.contains(":")) {
            throw new DishException("菜品数据格式错误：" + item);
        }

        System.out.println("解析成功：" + item);
    }

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseDish(item);
            } catch (DishException e) {
                System.err.println("[ERROR] 解析失败：" + e.getMessage());
                // 此处可扩展为写入日志文件或数据库
            }
        }
    }
}