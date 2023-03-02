package ca.lambton.habittracker.view.newhabit;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import ca.lambton.habittracker.databinding.FragmentNewHabitBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NewHabitFragment extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.lambton.habittracker.databinding.FragmentNewHabitBinding binding = FragmentNewHabitBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        FragmentManager supportFragmentManager = getSupportFragmentManager();
    }
}