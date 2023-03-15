package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ca.lambton.habittracker.habit.model.Habit;

@Dao
public abstract class HabitDao {

    @Insert
    public abstract void insertHabit(Habit... habit);

    @Delete
    public abstract void delete(Habit... habit);

    @Update
    public abstract void update(Habit... habit);

    @Query("SELECT * FROM HABIT_TBL WHERE CATEGORY_ID = :category")
    public abstract LiveData<List<Habit>> getAllHabitsByCategory(long category);

    @Query("SELECT * FROM HABIT_TBL WHERE ID = :id")
    public abstract LiveData<Habit> fetchById(Long id);

    @Query("SELECT * FROM HABIT_TBL WHERE name = :name")
    public abstract LiveData<Habit> fetchByName(String name);
}
