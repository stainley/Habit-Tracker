package ca.lambton.habittracker.habit.view.myhabits;

public class MyHabitsGridButton {

    private String buttonLabel;
    private int iconId;

    public MyHabitsGridButton(String buttonLabel, int iconId) {
        this.buttonLabel = buttonLabel;
        this.iconId = iconId;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
