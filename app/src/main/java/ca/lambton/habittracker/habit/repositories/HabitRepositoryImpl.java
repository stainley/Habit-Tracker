package ca.lambton.habittracker.habit.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Optional;

import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.common.db.AppDatabase;

public class HabitRepositoryImpl implements HabitRepository {

    private final HabitDao habitDao;

    public HabitRepositoryImpl(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        habitDao = db.habitDao();
    }

    @Override
    public void save(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.save(habit));
    }

    @Override
    public void update(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.update(habit));
    }

    @Override
    public void delete(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.delete(habit));
    }

    @Override
    public LiveData<List<Habit>> fetchAll() {
        return habitDao.fetchAll();
    }

    @Override
    public LiveData<Habit> fetchByName(@NonNull String name) {

        return habitDao.fetchByName(name);
    }

    @Override
    public LiveData<Habit> fetchByTitle(String title) {
        return null;
    }

    @Override
    public LiveData<Optional<Habit>> fetchById(Long id) {
        return habitDao.fetchById(id);
    }
}
