class PatientRecord {
    private int count = 100;

    public void update() {
        synchronized (this) {
            if (count > 0) {
                count--;
            }
        }
    }

    public int getCount() {
        return count;
    }
}