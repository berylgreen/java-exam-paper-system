class BookStock {
    private int stock = 100;

    public void updateStock() {
        synchronized (this) {
            if (stock > 0) {
                stock--;
            }
        }
    }

    public int getStock() {
        return stock;
    }
}