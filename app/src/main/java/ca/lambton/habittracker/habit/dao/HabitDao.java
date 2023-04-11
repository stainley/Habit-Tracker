package ca.lambton.habittracker.habit.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

@Dao
public abstract class HabitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertHabit(Habit habit);

    @Insert
    public abstract void insertProgress(List<Progress> progress);

    @Delete
    public abstract void delete(Habit... habit);

    @Update
    public abstract void update(Habit... habit);

    @Query("SELECT * FROM HABIT_TBL WHERE CATEGORY_ID = :category")
    public abstract LiveData<List<Habit>> getAllHabitsByCategory(long category);

    @Query("SELECT * FROM HABIT_TBL")
    public abstract LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_TYPE = 'PERSONAL' and USER_ID != -1")
    public abstract LiveData<List<Habit>> getAllPersonalHabits();

    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_TYPE = 'PERSONAL' AND USER_ID = :userId")
    public abstract LiveData<List<Habit>> getAllPersonalHabitsByUserId(String userId);

    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_TYPE = 'PUBLIC'")
    public abstract LiveData<List<Habit>> getAllPublicHabits();

    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_TYPE = 'PUBLIC' AND USER_ID = :userId")
    public abstract LiveData<List<Habit>> getAllPublicHabitsByUserId(String userId);

    @Query("SELECT * FROM HABIT_TBL WHERE HABIT_ID = :id")
    public abstract LiveData<Habit> fetchById(Long id);

    @Query("SELECT * FROM HABIT_TBL WHERE name = :name")
    public abstract LiveData<Habit> fetchByName(String name);

    @Query("SELECT * FROM HABIT_TBL WHERE PREDEFINED = :userID")
    public abstract LiveData<List<Habit>> fetchAllMyHabits(long userID);

    @Transaction
    public void insertProgressHabit(Habit habit, @NonNull List<Progress> progressList) {
        long habitId = insertHabit(habit);

        progressList.forEach(progress -> {
            progress.setHabitId(habitId);
        });

        insertProgress(progressList);
    }

    @Query("SELECT SUM(HAB.SCORE) FROM HABIT_TBL HAB WHERE HAB.USER_ID = :userId")
    public abstract LiveData<Integer> fetchScoreByUser(String userId);
}
