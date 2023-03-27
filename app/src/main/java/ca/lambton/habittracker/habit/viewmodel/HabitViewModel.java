package ca.lambton.habittracker.habit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.repository.HabitRepository;

public class HabitViewModel extends ViewModel {

    private final HabitRepository repository;

    public HabitViewModel(@NonNull Application application) {
        this.repository = new HabitRepository(application);
    }

    public LiveData<List<Habit>> getAllHabitByCategory(long categoryId) {
        return repository.getAllHabitByCategory(categoryId);
    }

    public LiveData<List<Habit>> getAllHabit() {
        return repository.getAllHabit();
    }

    public LiveData<Habit> getHabitById(long id) {
        return repository.getHabitById(id);
    }

    public void saveHabit(@NonNull Habit habit) {
        repository.save(habit);
    }

    public void updateHabit(@NonNull Habit habit) {
        repository.update(habit);
    }

    public void deleteHabit(@NonNull Habit habit) {
        repository.delete(habit);
    }

    public LiveData<Habit> getHabitByName(String name) {
        return repository.getHabitByName(name);
    }

    //FIXME: We need to use the user_id otherwise predefined
    public LiveData<List<Habit>> fetchAllMyHabits(long userId) {
        return repository.fetchAllMyHabit(userId);
    }


    public LiveData<List<HabitProgress>> getProgressByHabitId(long habitId) {
        return repository.getHabitProgress(habitId);
    }

    public LiveData<List<Progress>> getTodayAllMyHabitProgress() {
        return repository.getTodayAllMyHabitProgress();
    }

    public LiveData<List<HabitProgress>> getAllProgress() {
        return repository.getHabitProgress();
    }

    public List<HabitProgress> getHabitProgressNotLive() {
        return repository.getHabitProgressNotLive();
    }

    public void increase(Progress progress) {
        this.repository.increaseHabit(progress);
    }

    public void decrease(long progressId) {
        this.repository.decreaseHabit(progressId);
    }

    public void insertHabitProgress(Habit habit, List<Progress> progressList) {
        this.repository.insertHabitProgress(habit, progressList);
    }


}
