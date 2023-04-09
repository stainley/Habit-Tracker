package ca.lambton.habittracker.view.myhabits;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import ca.lambton.habittracker.databinding.FragmentMyHabitsBinding;

public class MyHabitsFragment extends Fragment {
    private static final String TAG = MyHabitsFragment.class.getName();
    private GridView myHabitsGridButton;
    private CategoryViewModel categoryViewModel;
    private FragmentMyHabitsBinding binding;
    private CategoryRecycleAdapter categoryListAdapter;
    RecyclerView categorySearchRecyclerView;
    private SearchBar searchBarCategory;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoriesFiltered = new ArrayList<>();

    ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    searchView.getEditText().setText(spokenText);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMyHabitsBinding.inflate(LayoutInflater.from(requireContext()));

        recyclerView = binding.categoryList;
        categorySearchRecyclerView = binding.categoryListFiltered;
        searchBarCategory = binding.searchCategoryBar;
        searchView = binding.searchView;
        myHabitsGridButton = binding.myHabitsGridView;

        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

        categoryListAdapter = new CategoryRecycleAdapter(categories, getOnCallbackCategory(categories));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(categoryListAdapter);

        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        searchView.inflateMenu(R.menu.search_bar_menu);
        searchView.setOnMenuItemClickListener(item -> {
            displaySpeechRecognizer();
            return true;
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayList<MyHabitsGridButton> myHabitsGridButtonModelArrayList = new ArrayList<>();
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Create new habit", R.drawable.ic_new_habit));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Due for today", R.drawable.ic_due_today));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Ongoing habit", R.drawable.ic_ongoing_habit));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Completed habits", R.drawable.ic_completed_habits));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Habit history", R.drawable.ic_habit_history));
        myHabitsGridButtonModelArrayList.add(new MyHabitsGridButton("Challenges & Leaderboard", R.drawable.ic_challenges_leaderboard));
        MyHabitsGridButtonAdapter adapter = new MyHabitsGridButtonAdapter(requireContext(), myHabitsGridButtonModelArrayList, getCallbackMyHabitsGridButton(myHabitsGridButtonModelArrayList));
        myHabitsGridButton.setAdapter(adapter);

        return binding.getRoot();
    }


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

                CategoryRecycleAdapter categoryListAdapterFiltered = new CategoryRecycleAdapter(categoriesFiltered, getOnCallbackCategory(categoriesFiltered));
                categorySearchRecyclerView.setAdapter(categoryListAdapterFiltered);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @NonNull
    private CategoryRecycleAdapter.OnCategoryCallback getOnCallbackCategory(List<ca.lambton.habittracker.category.model.Category> categories) {
        return position -> {
            categories.get(position);

        };
    }

    @NonNull
    private MyHabitsGridButtonAdapter.OnMyHabitsGridButtonCallback getCallbackMyHabitsGridButton(List<MyHabitsGridButton> myHabitsGridButton) {
        return position -> {
            switch (position) {
                case 0:
                    Navigation.findNavController(requireView()).navigate(R.id.newHabitFragment);
                    break;
                case 1:
                    Navigation.findNavController(requireView()).navigate(R.id.nav_due_today);
                    break;
                case 2:
                    Navigation.findNavController(requireView()).navigate(R.id.ongoingHabitFragment);
                    break;
                case 3:
                    Navigation.findNavController(requireView()).navigate(R.id.completeHabitCollectionFragment);
                    break;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        this.categories.clear();
        categoryViewModel.getAllCategories().observe(this, result -> {

            Log.i(TAG, "Habit Categories" + result.size());
            this.categories.addAll(result);
            categoryListAdapter.notifyItemRangeChanged(0, result.size());
        });
        recyclerView.setAdapter(categoryListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        //categoryListAdapter.notifyDataSetChanged();
    }
}
