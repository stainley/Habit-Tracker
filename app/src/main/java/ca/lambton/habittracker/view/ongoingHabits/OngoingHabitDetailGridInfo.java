package ca.lambton.habittracker.view.ongoingHabits;

public class OngoingHabitDetailGridInfo {

    private String cardLabel;
    private String progressText;

    public OngoingHabitDetailGridInfo(String cardLabel, String progressText) {
        this.cardLabel = cardLabel;
        this.progressText = progressText;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }
}