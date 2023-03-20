package ca.lambton.habittracker.habit.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.model.Habit;

public class HabitRepository {

    private HabitDao habitDao;

    public HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        habitDao = db.habitDao();
    }

    public LiveData<List<Habit>> getAllHabitByCategory(long category) {
        return habitDao.getAllHabitsByCategory(category);
    }

    public LiveData<List<Habit>> getAllHabit() {
        return habitDao.getAllHabits();
    }

    public LiveData<Habit> getHabitById(long id) {
        return habitDao.fetchById(id);
    }

    public LiveData<Habit> getHabitByName(String name) {
        return habitDao.fetchByName(name);
    }

    public void save(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.insertHabit(habit));
    }

    public void update(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.update(habit));
    }

    public void delete(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.delete(habit));
    }
}
