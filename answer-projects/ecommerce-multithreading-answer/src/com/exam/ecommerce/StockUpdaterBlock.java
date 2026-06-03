// 方式二：同步代码块
class StockUpdaterBlock {
    private int stock = 100;

    public void updateStock() {
        synchronized (this) {
            if (stock > 0) {
                stock--;
                System.out.println(Thread.currentThread().getName() + " updated stock, remaining: " + stock);
            }
        }
    }

    public int getStock() {
        return stock;
    }
}