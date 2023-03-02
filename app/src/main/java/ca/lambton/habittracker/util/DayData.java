package ca.lambton.habittracker.util;

public class DayData {
    private String mDay;
    private int mPercentage;
    private int dayNumber;

    public DayData(String day, int dayNumber, int percentage) {
        mDay = day;
        mPercentage = percentage;
        this.dayNumber = dayNumber;
    }

    public String getDay() {
        return mDay;
    }

    public int getPercentage() {
        return mPercentage;
    }

    public int getDayNumber() {
        return dayNumber;
    }
}
