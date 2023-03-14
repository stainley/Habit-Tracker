package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.common.dao.AbstractDao;

@Dao
public interface HabitDao extends AbstractDao<Habit> {

    @Insert
    @Override
    void save(Habit type);

    @Delete
    @Override
    void delete(Habit type);

    @Update
    @Override
    void update(Habit type);

    @Query("SELECT * FROM HABIT_TBL")
    @Override
    LiveData<List<Habit>> fetchAll();

    @Query("SELECT * FROM HABIT_TBL WHERE ID = :id")
    @Override
    LiveData<Optional<Habit>> fetchById(Long id);

    @Query("SELECT * FROM HABIT_TBL WHERE name = :name")
    LiveData<Habit> fetchByName(String name);
}
