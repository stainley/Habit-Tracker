package ca.lambton.habittracker.util.calendar.monthly;

public class CalendarData {

    private String progress;
    private String date;

    public CalendarData(String progress, String date) {
        this.progress = progress;
        this.date = date;
    }

    public String getProgress() {
        return progress;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "CalendarData{" +
                "progress='" + progress + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
