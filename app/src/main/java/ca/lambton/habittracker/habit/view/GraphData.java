package ca.lambton.habittracker.habit.view;

import java.io.Serializable;

public class GraphData implements Serializable {
    private final int percentage;
    private final String period;

    public GraphData(int percentage, String period) {
        this.percentage = percentage;
        this.period = period;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getPeriod() {
        return period;
    }

}
