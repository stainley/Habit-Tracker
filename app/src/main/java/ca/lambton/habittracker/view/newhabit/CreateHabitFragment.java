package ca.lambton.habittracker.view.newhabit;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Duration;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.HabitType;

public class CreateHabitFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private CategoryViewModel categoryViewModel;
    FragmentCreateHabitLayoutBinding binding;
    private Duration durationUnit = Duration.MINUTES;
    private HabitType habitType = HabitType.PERSONAL;
    private Frequency frequencyUnit = Frequency.DAILY;

    long categoryId = -1;
    String[] categories = new String[0];
    ArrayAdapter<String> categoryDropDownAdapter;
    private MaterialButton personalHabitType;
    MaterialButton publicHabitType;

    private FirebaseUser mUser;

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        habitViewModel = new ViewModelProvider(new ViewModelStore(), new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);

        View view = inflater.inflate(R.layout.fragment_create_habit_layout, container, false);
        binding = FragmentCreateHabitLayoutBinding.inflate(inflater, container, false);

        EditText editTextStartDate = binding.editTextStartDate;
        editTextStartDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view12, year1, monthOfYear, dayOfMonth) -> {
                // set the selected date to the EditText
                editTextStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
            }, year, month, day);

            datePickerDialog.show();
        });

        EditText editTextEndDate = binding.editTextEndDate;
        editTextEndDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view13, year12, monthOfYear, dayOfMonth) -> {
                // set the selected date to the EditText
                editTextEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year12);
            }, year, month, day);

            datePickerDialog.show();
        });

        binding.createButton.setOnClickListener(this::createHabit);

        binding.durationUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.durationUnitMinutes) {
                durationUnit = Duration.MINUTES;
            } else if (checkedId == R.id.durationUnitHours) {
                durationUnit = Duration.HOURS;
            }
        });

        binding.frequencyUnitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.frequencyUnitDay) {
                    frequencyUnit = Frequency.DAILY;
                } else if (checkedId == R.id.frequencyUnitWeek) {
                    frequencyUnit = Frequency.WEEKLY;
                } else if (checkedId == R.id.frequencyUnitMonth) {
                    frequencyUnit = Frequency.MONTHLY;
                }
            }
        });

        categoryViewModel = new ViewModelProvider(new ViewModelStore(), new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), result -> {
            String[] newCategories = new String[categories.length + result.size()];

            for (int i = 0; i < result.size(); i++) {
                newCategories[i] = result.get(i).getName();
            }
            categories = newCategories;

            categoryDropDownAdapter = new ArrayAdapter<>(getContext(), R.layout.categories_dropdown_items, categories);
            AutoCompleteTextView autoCompleteTextView = binding.autoCompleteTxt;
            autoCompleteTextView.setAdapter(categoryDropDownAdapter);

            autoCompleteTextView.setOnItemClickListener((adapterView, view1, position1, id) -> {
                String category = adapterView.getItemAtPosition(position1).toString();

                categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result1 -> {
                    categoryId = result1.getId();
                });
            });
        });

        personalHabitType = binding.personalHabitType;
        personalHabitType.setOnClickListener(this::habitTypeSelection);
        publicHabitType = binding.publicHabitType;
        publicHabitType.setOnClickListener(this::habitTypeSelection);

        return binding.getRoot();
    }

    private void habitTypeSelection(View view) {

        ColorStateList selectedTextColorBlack = ColorStateList.valueOf(getResources().getColor(R.color.black, getResources().newTheme()));
        ColorStateList selectedTextColorWhite = ColorStateList.valueOf(getResources().getColor(R.color.white, getResources().newTheme()));

        ColorStateList colorStateListDark = ColorStateList.valueOf(getResources().getColor(R.color.md_theme_light_primary));

        if (view.getId() == R.id.personalHabitType) {
            personalHabitType.setBackgroundColor(getContext().getColor(R.color.md_theme_light_primary));
            publicHabitType.setBackgroundColor(getContext().getColor(R.color.md_theme_light_onPrimary));
            personalHabitType.setTextColor(selectedTextColorWhite);
            publicHabitType.setTextColor(selectedTextColorBlack);
            publicHabitType.setEnabled(true);
            publicHabitType.setStrokeColor(colorStateListDark);
            personalHabitType.setEnabled(false);
            habitType = HabitType.PERSONAL;

        } else {
            personalHabitType.setBackgroundColor(getContext().getColor(R.color.md_theme_light_onPrimary));
            publicHabitType.setBackgroundColor(getContext().getColor(R.color.md_theme_light_primary));
            publicHabitType.setTextColor(selectedTextColorWhite);
            personalHabitType.setTextColor(selectedTextColorBlack);
            personalHabitType.setEnabled(true);
            personalHabitType.setStrokeColor(colorStateListDark);
            publicHabitType.setEnabled(false);
            habitType = HabitType.PUBLIC;
        }
    }

    private void createHabit(View view) {

        if (binding.titleHabit.getText().toString().equals("") || binding.frequencyText.getText().toString().equals("") || binding.editTextStartDate.getText().toString().equals("") || binding.editTextStartDate.getText().toString().equals("")) {
            Toast.makeText(requireContext(), "Some fields are required", Toast.LENGTH_SHORT).show();
            return;
        }


        Habit newHabit = new Habit();
        newHabit.setUserId(mUser != null ? mUser.getUid() : "");
        newHabit.setName(binding.titleHabit.getText().toString());
        newHabit.setDescription(binding.description.getText().toString());
        int duration = binding.durationText.getText().toString().equals("") ? 0 : Integer.parseInt(binding.durationText.getText().toString());
        newHabit.setDuration(duration);
        newHabit.setDurationUnit(durationUnit.name());
        newHabit.setCreationDate(new Date().getTime());
        int frequency = binding.frequencyText.getText().toString().equals("") ? 0 : Integer.parseInt(binding.frequencyText.getText().toString());
        newHabit.setFrequency(frequency);
        newHabit.setFrequencyUnit(frequencyUnit.name());

        newHabit.setHabitType(habitType.name());
        newHabit.setCategoryId(categoryId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate;
        Date endDate;

        try {
            String startDateTxt = binding.editTextStartDate.getText().toString();
            String endDateTxt = binding.editTextEndDate.getText().toString();
            if (!startDateTxt.equals("")) {
                startDate = simpleDateFormat.parse(startDateTxt);
                if (startDate != null) newHabit.setStartDate(startDate.getTime());
            }

            if (!endDateTxt.equals("")) {
                endDate = simpleDateFormat.parse(endDateTxt);
                if (endDate != null) newHabit.setEndDate(endDate.getTime());
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (newHabit.getName().isEmpty() || newHabit.getName() == null) {
            habitViewModel.saveHabit(newHabit);
        } else {
            habitViewModel.saveHabit(newHabit);
        }

        Toast.makeText(requireContext(), "New habit registered", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack(R.id.createHabitFragment, true);
    }
}
