package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ca.lambton.habittracker.habit.model.Habit;

@Dao
public abstract class HabitDao {

    @Insert
    public abstract void insertHabit(Habit... habit);

    @Query("SELECT * FROM HABIT_TBL WHERE CATEGORY_ID = :category")
    public abstract LiveData<List<Habit>> getAllHabitsByCategory(long category);
}
