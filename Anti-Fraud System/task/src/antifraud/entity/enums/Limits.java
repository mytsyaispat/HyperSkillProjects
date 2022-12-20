package antifraud.entity.enums;

public enum Limits {

    ALLOWED(200),
    MANUAL_PROCESSING(1500);

    private long value;
    Limits(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void increasing(long value) {
        this.value = (long) Math.ceil(0.8 * this.value + 0.2 * value);
    }

    public void decreasing(long value) {
        this.value = (long) Math.ceil(0.8 * this.value - 0.2 * value);
    }
}
