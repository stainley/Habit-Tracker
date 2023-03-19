package ca.lambton.habittracker.view.newhabit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Duration;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.HabitType;

public class CreateHabitFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private CategoryViewModel categoryViewModel;
    FragmentCreateHabitLayoutBinding binding;
    int durationUnit = Duration.MINUTES.ordinal();
    int frequencyUnit = Frequency.DAILY.ordinal();
    int habitType = HabitType.PERSONAL.ordinal();

    long categoryId = -1;

    String[] categories = new String[1000];

    ArrayAdapter<String> categoryDropDownAdapter;

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
        categoryViewModel = new ViewModelProvider(new ViewModelStore(), new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(this, result -> {
            for (int i = 0; i < result.size(); i++) {
                this.categories[i] = result.get(i).getName();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        habitViewModel = new ViewModelProvider(new ViewModelStore(), new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);

        View view = inflater.inflate(R.layout.fragment_create_habit_layout, container, false);
        binding = FragmentCreateHabitLayoutBinding.inflate(inflater, container, false);

        categoryDropDownAdapter = new ArrayAdapter<>(getContext(), R.layout.categories_dropdown_items, categories);
        AutoCompleteTextView autoCompleteTextView = binding.autoCompleteTxt;
        autoCompleteTextView.setAdapter(categoryDropDownAdapter);

        autoCompleteTextView.setOnItemClickListener((adapterView, view1, position1, id) -> {
            String category = adapterView.getItemAtPosition(position1).toString();

            categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result -> {
                categoryId = result.getId();
            });
        });

        EditText editTextStartDate = binding.editTextStartDate;
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        (view12, year1, monthOfYear, dayOfMonth) -> {
                            // set the selected date to the EditText
                            editTextStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        EditText editTextEndDate = binding.editTextEndDate;
        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set the selected date to the EditText
                                editTextEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        binding.createButton.setOnClickListener(this::createHabit);

        binding.durationUnitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.durationUnitMinutes:
                        durationUnit = Duration.MINUTES.ordinal();
                        break;
                    case R.id.durationUnitHours:
                        durationUnit = Duration.HOURS.ordinal();
                        break;
                }
            }
        });

        binding.frequencyUnitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.frequencyUnitDay:
                        frequencyUnit = Frequency.DAILY.ordinal();
                        break;
                    case R.id.frequencyUnitWeek:
                        frequencyUnit = Frequency.WEEKLY.ordinal();
                        break;
                    case R.id.frequencyUnitMonth:
                        frequencyUnit = Frequency.MONTHLY.ordinal();
                        break;
                }
            }
        });

        return binding.getRoot();
    }

    private void createHabit(View view) {
        Habit newHabit = new Habit();
        newHabit.setName(binding.titleHabit.getText().toString());
        newHabit.setDescription(binding.description.getText().toString());
        newHabit.setDuration(binding.durationText.getText().toString());
        newHabit.setDurationUnit(durationUnit);
        newHabit.setFrequency(binding.frequencyText.getText().toString());
        newHabit.setFrequencyUnit(frequencyUnit);
        newHabit.setHabitType(habitType);
        newHabit.setCategoryId(categoryId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate;
        Date endDate;
        try {
            startDate = simpleDateFormat.parse(binding.editTextStartDate.getText().toString());
            endDate = simpleDateFormat.parse(binding.editTextEndDate.getText().toString());
            System.out.println(startDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        newHabit.setStartDate(startDate.getTime());
        newHabit.setEndDate(endDate.getTime());

        if (newHabit.getName().isEmpty() || newHabit.getName() == null) {
            //habitViewModel.saveHabit(newHabit);
            List<Progress> progressList = new ArrayList<>();
            Progress progress = new Progress();
            progress.setCounter(0);
            progress.setUpdatedDate(new Date().getTime());
            progressList.add(progress);
            habitViewModel.insertHabitProgress(newHabit, progressList);
        } else {
            //habitViewModel.saveHabit(newHabit);
            List<Progress> progressList = new ArrayList<>();
            Progress progress = new Progress();
            progress.setCounter(0);
            progress.setUpdatedDate(new Date().getTime());
            progressList.add(progress);
            habitViewModel.insertHabitProgress(newHabit, progressList);
        }
        //Navigation.findNavController(view).navigate(R.id.action_createHabitFragment_to_habitListFragment);
    }
}
