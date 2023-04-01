package ca.lambton.habittracker.view.newhabit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCreateHabitLayoutBinding.inflate(LayoutInflater.from(requireContext()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        EditText editTextStartDate = binding.editTextStartDate;
        editTextStartDate.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 1);
            constraintsBuilder.setStart(startDate.getTimeInMillis());
            constraintsBuilder.setEnd(endDate.getTimeInMillis());
            constraintsBuilder.setValidator(DateValidatorPointForward.now());
            CalendarConstraints constraints = constraintsBuilder.build();

            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
            builder.setCalendarConstraints(constraints);
            MaterialDatePicker datePicker = builder.build();
            datePicker.show(getChildFragmentManager(), "datePicker");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                selectedDate.setTimeInMillis((Long) selection);

                TimeZone deviceTimeZone = TimeZone.getDefault();
                int offsetFromUTC = deviceTimeZone.getOffset(selectedDate.getTimeInMillis());
                selectedDate.add(Calendar.MILLISECOND, offsetFromUTC);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String selectedDateStr = dateFormat.format(selectedDate.getTime());

                editTextStartDate.setText(selectedDateStr);
            });
        });

        EditText editTextEndDate = binding.editTextEndDate;
        editTextEndDate.setOnClickListener(v -> {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate1;

                String startDateTxt = binding.editTextStartDate.getText().toString();
                startDate1 = simpleDateFormat.parse(startDateTxt);

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.YEAR, 1);
                constraintsBuilder.setStart(startDate.getTimeInMillis());
                constraintsBuilder.setEnd(endDate.getTimeInMillis());
                constraintsBuilder.setValidator(DateValidatorPointForward.from(startDate1.getTime()));
                CalendarConstraints constraints = constraintsBuilder.build();

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setCalendarConstraints(constraints);
                MaterialDatePicker datePicker = builder.build();
                datePicker.show(getChildFragmentManager(), "datePicker");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTimeInMillis((Long) selection);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String selectedDateStr = dateFormat.format(selectedDate.getTime());

                    editTextEndDate.setText(selectedDateStr);
                });
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        binding.createButton.setOnClickListener(this::createHabit);

        binding.durationUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.durationUnitMinutes) {
                durationUnit = Duration.MINUTES;
            } else if (checkedId == R.id.durationUnitHours) {
                durationUnit = Duration.HOURS;
            }
        });

        binding.frequencyUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.frequencyUnitDay) {
                frequencyUnit = Frequency.DAILY;
            } else if (checkedId == R.id.frequencyUnitWeek) {
                frequencyUnit = Frequency.WEEKLY;
            } else if (checkedId == R.id.frequencyUnitMonth) {
                frequencyUnit = Frequency.MONTHLY;
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

        if (binding.titleHabit.getText().toString().equals("") || binding.frequencyText.getText().toString().equals("") || binding.editTextStartDate.getText().toString().equals("") || binding.editTextEndDate.getText().toString().equals("") || binding.autoCompleteTxt.getText().toString().equals("")) {

            Toast.makeText(requireContext(), "Some fields are required", Toast.LENGTH_SHORT).show();
            binding.titleHabit.setError("This field is required");
            binding.frequencyText.setError("This field is required");
            binding.editTextStartDate.setError("This field is required");
            binding.editTextEndDate.setError("This field is required");
            binding.autoCompleteTxt.setError("This field is required");

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

        habitViewModel.saveHabit(newHabit);

        if (newHabit.getHabitType().equalsIgnoreCase(HabitType.PUBLIC.toString())) {
            habitViewModel.saveCloudHabit(newHabit);
        }

        Toast.makeText(requireContext(), "New habit registered", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack(R.id.createHabitFragment, true);
    }
}
