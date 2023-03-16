package ca.lambton.habittracker.util;

public enum HabitType {
    PERSONAL("Personal"), PUBLIC("Public");

    HabitType(String name) {
        this.name = name;
    }
    private final String name;
    public String getName() {
        return this.name;
    }
}
