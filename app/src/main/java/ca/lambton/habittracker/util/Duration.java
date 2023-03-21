package ca.lambton.habittracker.util;

public enum Duration {
    MINUTES("Minutes"), HOURS("Hours");

    Duration(String name) {
        this.name = name;
    }
    private final String name;
    public String getName() {
        return this.name;
    }
}
