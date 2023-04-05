package ca.lambton.habittracker.habit.view.fragment.fragment.habit;

import androidx.annotation.DrawableRes;

public class HabitCard {
    private final String habitName;
    @DrawableRes
    private final int habitPicture;


    public HabitCard(String habitName, int habitPicture) {
        this.habitName = habitName;
        this.habitPicture = habitPicture;
    }


    public String getHabitName() {
        return habitName;
    }

    public int getHabitPicture() {
        return habitPicture;
    }

}
