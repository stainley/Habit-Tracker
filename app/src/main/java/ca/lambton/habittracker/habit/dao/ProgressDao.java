package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;

import java.util.List;

import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

@Dao
public abstract class ProgressDao {

    @Insert
    public abstract void insertProgress(Progress progress);

    @Query("SELECT * FROM PROGRESS_TBL WHERE PARENT_HABIT_ID  = :habitId")
    public abstract LiveData<Progress> getProgress(long habitId);

    @Transaction
    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_ID = :habitId")
    public abstract LiveData<List<HabitProgress>> getProgressByHabit(long habitId);

    @Transaction
    @Query("SELECT * FROM HABIT_TBL WHERE PREDEFINED = 0")
    public abstract LiveData<List<HabitProgress>> getAllProgress();

    @Query("DELETE FROM PROGRESS_TBL WHERE PROGRESS_ID = :progressId")
    public abstract void decreaseProgress(long progressId);
}
