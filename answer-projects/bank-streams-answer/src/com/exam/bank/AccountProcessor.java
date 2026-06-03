import java.util.List;
import java.util.stream.Collectors;

class Account {
    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class AccountProcessor {
    public List<String> processList(List<Account> list) {
        return list.stream()
                .filter(Account::isValid)
                .map(Account::getName)
                .collect(Collectors.toList());
    }
}