package ca.lambton.habittracker.view.newhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.view.myhabits.CategoryRecycleAdapter;
import ca.lambton.habittracker.view.myhabits.MyHabitsFragment;
import ca.lambton.habittracker.view.myhabits.MyHabitsGridButton;
import ca.lambton.habittracker.view.myhabits.MyHabitsGridButtonAdapter;

public class CreateHabitFragment  extends Fragment {

    public CreateHabitFragment() {
    }

    public static CreateHabitFragment newInstance(String param1, String param2) {
        CreateHabitFragment fragment = new CreateHabitFragment();
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

        View view = inflater.inflate(R.layout.fragment_create_habit_layout, container, false);

        return view;
    }

}
