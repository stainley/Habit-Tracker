package ca.lambton.habittracker;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import ca.lambton.habittracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

    }

}