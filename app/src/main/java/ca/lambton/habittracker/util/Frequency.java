package ca.lambton.habittracker.util;

public enum Frequency {
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly");

    Frequency(String name) {
        this.name = name;
    }
    private final String name;
    public String getName() {
        return this.name;
    }
}
