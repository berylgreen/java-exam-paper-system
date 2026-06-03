abstract class Account {
    /**
     * 账户统计处理行为，不同账户类型有不同实现
     */
    public abstract void process();
}

class RegularAccount extends Account {
    @Override
    public void process() {
        System.out.println("处理普通账户的统计逻辑");
    }
}

class VIPAccount extends Account {
    @Override
    public void process() {
        System.out.println("处理 VIP 账户的统计逻辑");
    }
}

class EnterpriseAccount extends Account {
    @Override
    public void process() {
        System.out.println("处理企业账户的统计逻辑");
    }
}

public class AccountStatistics {
    public void processAll(Account[] accounts) {
        for (Account account : accounts) {
            account.process();
        }
    }

    public static void main(String[] args) {
        Account[] accounts = {
            new RegularAccount(),
            new VIPAccount(),
            new EnterpriseAccount()
        };

        AccountStatistics statistics = new AccountStatistics();
        statistics.processAll(accounts);
    }
}