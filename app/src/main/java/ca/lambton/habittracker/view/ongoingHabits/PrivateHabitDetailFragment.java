package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.lambton.habittracker.R;

public class PrivateHabitDetailFragment extends Fragment {

    public PrivateHabitDetailFragment() {
        // Required empty public constructor
    }

    public static PrivateHabitDetailFragment newInstance(String param1, String param2) {
        PrivateHabitDetailFragment fragment = new PrivateHabitDetailFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_habit_detail, container, false);
    }
}