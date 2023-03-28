package ca.lambton.habittracker.habit.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.dao.ProgressDao;
import ca.lambton.habittracker.habit.dao.UserDao;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.model.User;

public class HabitRepository {

    private HabitDao habitDao;
    private ProgressDao progressDao;

    private UserDao userDao;

    public HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        habitDao = db.habitDao();
        progressDao = db.progressDao();
        userDao = db.userDao();
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

    public LiveData<List<HabitProgress>> getHabitProgress(long habitId) {
        return progressDao.getProgressByHabit(habitId);
    }


    public LiveData<List<HabitProgress>> getHabitProgress() {
        return progressDao.getAllProgress();
    }

    public List<HabitProgress> getHabitProgressNotLive() {
        List<HabitProgress> allProgressNotLive = new ArrayList<>();

        AppDatabase.databaseWriterExecutor.execute(() -> {
            allProgressNotLive.addAll(progressDao.getAllProgressNotLive());
        });

        return allProgressNotLive;
    }

    public LiveData<List<Habit>> fetchAllMyHabit(long userId) {
        return habitDao.fetchAllMyHabits(userId);
    }

    public void increaseHabit(Progress progress) {
        AppDatabase.databaseWriterExecutor.execute(() -> progressDao.insertProgress(progress));
    }

    public void decreaseHabit(long progressId) {
        AppDatabase.databaseWriterExecutor.execute(() -> progressDao.decreaseProgress(progressId));
    }

    public void insertHabitProgress(Habit habit, List<Progress> progressList) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.insertProgressHabit(habit, progressList));
    }

    public LiveData<List<Progress>> getTodayAllMyHabitProgress() {

        return progressDao.getTodayAllMyHabitProgress();
    }

    public void saveUser(User user) {
        AppDatabase.databaseWriterExecutor.execute(() -> userDao.saveUser(user));
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
}
