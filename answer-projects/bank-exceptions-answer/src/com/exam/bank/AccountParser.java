class AccountException extends Exception {
    public AccountException(String message) {
        super(message);
    }
}

public class AccountParser {
    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parse(item);
                System.out.println("Parsed: " + item);
            } catch (AccountException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void parse(String item) throws AccountException {
        if (item == null || item.trim().isEmpty()) {
            throw new AccountException("账户数据格式错误");
        }
    }
}