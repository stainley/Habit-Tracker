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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHabitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHabitsFragment extends Fragment {
    private GridView myHabitsGridButton;
    private CategoryViewModel categoryViewModel;
    private CategoryRecycleAdapter categoryListAdapter;
    RecyclerView categorySearchRecyclerView;
    private SearchBar searchBarCategory;
    private SearchView searchView;

    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoriesFiltered = new ArrayList<>();

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

        searchView.inflateMenu(R.menu.search_bar_menu);
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

        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), result -> {
            this.categories.clear();
            this.categories.addAll(result);
            categoryListAdapter.notifyDataSetChanged();
        });

        categoryListAdapter = new CategoryRecycleAdapter(categories, getOnCallbackCategory(categories), this.getContext());
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
                categoriesFiltered.clear();
                categorySearchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

                categoriesFiltered = categories.stream().filter(category -> {
                    if (s.length() == 0) return false;
                    return category.getName().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                categoryListAdapter = new CategoryRecycleAdapter(categoriesFiltered, getOnCallbackCategory(categoriesFiltered), getContext());
            }

            @Override
            public void afterTextChanged(Editable s) {
                categorySearchRecyclerView.setAdapter(categoryListAdapter);
            }
        };
    }

    @NonNull
    private CategoryRecycleAdapter.OnCategoryCallback getOnCallbackCategory(List<ca.lambton.habittracker.category.model.Category> categories) {
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
