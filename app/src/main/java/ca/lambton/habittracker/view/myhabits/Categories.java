package ca.lambton.habittracker.view.myhabits;

public class Categories {
    private String name;
    private int duration;
    private String interval;
    private int iconId;

    public Categories(String name, int duration, String interval, int iconId) {
        this.name = name;
        this.duration = duration;
        this.interval = interval;
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
