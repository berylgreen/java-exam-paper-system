class BankAccount {
    private int balance = 100;

    public void withdraw(int amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance -= amount;
                System.out.println(Thread.currentThread().getName()
                        + " 取款 " + amount + "，余额：" + balance);
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " 取款失败，余额不足，当前余额：" + balance);
            }
        }
    }

    public int getBalance() {
        return balance;
    }
}