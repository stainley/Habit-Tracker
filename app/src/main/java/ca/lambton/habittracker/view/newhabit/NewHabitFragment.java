package ca.lambton.habittracker.view.newhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ca.lambton.habittracker.R;

public class NewHabitFragment extends Fragment {

    public NewHabitFragment() {
    }

    public static NewHabitFragment newInstance(String param1, String param2) {
        NewHabitFragment fragment = new NewHabitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_habit, container, false);

        return view;

    }
}