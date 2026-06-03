class CarInventory {
    private int stock = 100;

    public void rentCar() {
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