class DeviceUpdater {
    private int stock = 100;

    public void updateStock() {
        synchronized (this) {
            if (stock > 0) {
                stock--;
                System.out.println(Thread.currentThread().getName() + " 更新后库存：" + stock);
            }
        }
    }

    public int getStock() {
        return stock;
    }
}