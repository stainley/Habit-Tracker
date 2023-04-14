package ca.lambton.habittracker.habit.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Comparator;

@Entity(tableName = "PROGRESS_TBL",
        foreignKeys = @ForeignKey(entity = Habit.class, parentColumns = "HABIT_ID", childColumns = "PARENT_HABIT_ID", onDelete = ForeignKey.CASCADE),
        indices = @Index(value = "PARENT_HABIT_ID", name = "PROGRESS_IDX")
)
public class Progress implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PROGRESS_ID")
    private long progressId;
    @ColumnInfo(name = "PARENT_HABIT_ID")
    private long habitId;
    private boolean isCompleted;
    private int counter;
    @ColumnInfo(name = "UPDATE_DATE_TIME")
    private long updatedDate;
    @ColumnInfo(name = "UPDATED_DATE")
    private String date;
    @ColumnInfo(name = "UPDATED_TIME")
    private String time;

    public long getProgressId() {
        return progressId;
    }

    public void setProgressId(long progressId) {
        this.progressId = progressId;
    }

    public long getHabitId() {
        return habitId;
    }

    public void setHabitId(long habitId) {
        this.habitId = habitId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
