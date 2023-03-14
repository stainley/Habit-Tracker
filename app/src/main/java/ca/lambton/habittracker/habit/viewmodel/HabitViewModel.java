package ca.lambton.habittracker.habit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.repositories.HabitRepository;
import ca.lambton.habittracker.habit.repositories.HabitRepositoryImpl;

public class HabitViewModel extends ViewModel {

    private final HabitRepository habitRepository;

    public HabitViewModel(Application application) {
        this.habitRepository = new HabitRepositoryImpl(application);
    }

    public void createHabit(@NonNull Habit habit) {
        habitRepository.save(habit);
    }

    public void updateHabit(@NonNull Habit habit) {
        habitRepository.update(habit);
    }

    public LiveData<List<Habit>> getAllHabits() {
        return habitRepository.fetchAll();
    }

    public LiveData<Habit> getHabitByName(String name) {
        return habitRepository.fetchByName(name);
    }

    public void deleteHabit(@NonNull Habit habit) {
        habitRepository.delete(habit);
    }
}
