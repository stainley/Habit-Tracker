package ca.lambton.habittracker.habit.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class HabitProgress implements Serializable {
    @Embedded
    private Habit habit;
    @Relation(parentColumn = "HABIT_ID", entityColumn = "PARENT_HABIT_ID", entity = Progress.class)
    private List<Progress> progressList;


    @Ignore
    public HabitProgress() {
    }

    public HabitProgress(Habit habit, List<Progress> progressList) {
        this.habit = habit;
        this.progressList = progressList;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public List<Progress> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<Progress> progressList) {
        this.progressList = progressList;
    }
}
