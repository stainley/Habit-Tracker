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
}
