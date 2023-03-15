package ca.lambton.habittracker.habit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.repository.HabitRepository;

public class HabitViewModel extends ViewModel {

    private final HabitRepository repository;

    public HabitViewModel(@NonNull Application application) {
        this.repository = new HabitRepository(application);
    }

    public LiveData<List<Habit>> getAllHabitByCategory(long categoryId) {
        return repository.getAllHabitByCategory(categoryId);
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
}
