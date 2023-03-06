package ca.lambton.habittracker.view.myhabits;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ca.lambton.habittracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHabitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHabitsFragment extends Fragment {
    private GridView myHabitsGridButton;

    private CategoryRecycleAdapter categoryListAdapter;
    RecyclerView categorySearchRecyclerView;
    private SearchBar searchBarCategory;
    private SearchView searchView;

    public MyHabitsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyHabitsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyHabitsFragment newInstance(String param1, String param2) {
        MyHabitsFragment fragment = new MyHabitsFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_habits, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.categoryList);
        categorySearchRecyclerView = (RecyclerView) view.findViewById(R.id.categoryListFiltered);
        searchBarCategory = (SearchBar) view.findViewById(R.id.searchCategoryBar);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        searchView.setOnMenuItemClickListener(item -> {
            displaySpeechRecognizer();
            return true;
        });

        myHabitsGridButton = (GridView) view.findViewById(R.id.myHabitsGridView);
        ArrayList<MyHabitsGridButton> myHabitsGridButtonModelArrayList = new ArrayList<MyHabitsGridButton>();
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Create new habit", R.drawable.ic_new_habit));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Due for today", R.drawable.ic_due_today));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Ongoing habit", R.drawable.ic_ongoing_habit));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Completed habits", R.drawable.ic_completed_habits));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Habit history", R.drawable.ic_habit_history));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Challenges & Leaderboard", R.drawable.ic_challenges_leaderboard));
        MyHabitsGridButtonAdapter adapter = new MyHabitsGridButtonAdapter(getContext(), myHabitsGridButtonModelArrayList, getCallbackMyHabitsGridButton(myHabitsGridButtonModelArrayList));
        myHabitsGridButton.setAdapter(adapter);

        ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(new Category("Short Duration", 2, "5 - 10 mins", R.drawable.short_duration));
        categories.add(new Category("Long Duration", 3, "20 mins - 1 hour", R.drawable.long_duration));
        categories.add(new Category("Hobbies", 3, "30 mins", R.drawable.hobbies));
        categories.add(new Category("Outdoor Activities", 3, "20 mins - 1 hour", R.drawable.outdoor_activities));
        categories.add(new Category("Quit Bad Habits", 2, "20 mins - 1 hour", R.drawable.quit_bad_habits));
        categories.add(new Category("Food Habits", 2, "20 mins - 1 hour", R.drawable.food_habits));
        categories.add(new Category("Socialize", 2, "20 mins - 1 hour", R.drawable.socialize));
        categories.add(new Category("Relaxation", 3, "20 mins - 1 hour", R.drawable.relaxation));
        categories.add(new Category("Physical Health", 3, "20 mins - 1 hour", R.drawable.physical_health));
        categories.add(new Category("Mental Health", 3, "20 mins - 1 hour", R.drawable.mental_health));
        categories.add(new Category("Daily", 0, "20 mins - 1 hour", R.drawable.daily));
        categories.add(new Category("Weekly", 0, "20 mins - 1 hour", R.drawable.weekly));
        categories.add(new Category("Monthly", 0, "20 mins - 1 hour", R.drawable.monthly));
        categories.add(new Category("Self Care", 2, "10 - 30 mins", R.drawable.self_care));

        categoryListAdapter = new CategoryRecycleAdapter(categories, getOnCallbackCategory(categories));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(categoryListAdapter);

        return view;
    }

    ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    searchView.getEditText().setText(spokenText);
                }
    });


    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        textToSpeakLauncher.launch(intent);
    }

    private Supplier<TextWatcher> getTextWatcherSupplier() {

        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categorySearchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    @NonNull
    private CategoryRecycleAdapter.OnCategoryCallback getOnCallbackCategory(List<Category> categories) {
        return new CategoryRecycleAdapter.OnCategoryCallback() {

            @Override
            public void onRowClicked(int position) {

            }
        };
    }

    @NonNull
    private MyHabitsGridButtonAdapter.OnMyHabitsGridButtonCallback getCallbackMyHabitsGridButton(List<MyHabitsGridButton> myHabitsGridButton) {
        return new MyHabitsGridButtonAdapter.OnMyHabitsGridButtonCallback() {

            @Override
            public void onRowClicked(int position) {
                switch (position) {
                    case 0:
                        Navigation.findNavController(getView()).navigate(R.id.newHabitFragment);

                        break;
                }
            }
        };
    }
}
