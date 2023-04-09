package ca.lambton.habittracker.habit.view.newhabit;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.AlarmReceiver;
import ca.lambton.habittracker.util.Duration;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.HabitType;

public class CreateHabitFragment extends Fragment {
    private static final String TAG = CreateHabitFragment.class.getName();
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
    private MaterialButton publicHabitType;
    private FirebaseUser mUser;
    private ImageView detailImage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;
    private String pathUri;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private MaterialSwitch reminderSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCreateHabitLayoutBinding.inflate(LayoutInflater.from(requireContext()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        detailImage = binding.detailImage;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Create a Cloud Storage reference from the app
        storageRef = FirebaseStorage.getInstance().getReference();

        binding.overflowMenu.setOnClickListener(replaceImage());

        binding.selectTimeBtn.setOnClickListener(this::showTimePicker);
        reminderSwitch = binding.reminderSwitch;

        notificationChannel();
    }

    @NonNull
    private View.OnClickListener replaceImage() {
        return view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.overflow_habit_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.edit_image) {
                    addPhotoFromLibrary();
                    Toast.makeText(requireContext(), "Implement delete functionality", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        };
    }

    public void addPhotoFromLibrary() {
        System.out.println("ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) " + ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        EditText editTextStartDate = binding.editTextStartDate;
        editTextStartDate.setOnClickListener(v -> {
            editTextStartDate.setError(null);
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 1);
            constraintsBuilder.setStart(startDate.getTimeInMillis());
            constraintsBuilder.setEnd(endDate.getTimeInMillis());
            constraintsBuilder.setValidator(DateValidatorPointForward.now());
            CalendarConstraints constraints = constraintsBuilder.build();

            // Here is the problem with TimeZone
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setCalendarConstraints(constraints);

            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getChildFragmentManager(), "datePicker");

            datePicker.addOnPositiveButtonClickListener(selection -> {

                Calendar selectedDate = Calendar.getInstance(Locale.CANADA);
                selectedDate.setTimeInMillis(selection);


                TimeZone deviceTimeZone = TimeZone.getTimeZone("UTC");
                int offsetFromUTC = deviceTimeZone.getOffset(selection);
                selectedDate.add(Calendar.MILLISECOND, offsetFromUTC);
                selectedDate.add(Calendar.HOUR, 4);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDateStr = dateFormat.format(selectedDate.getTime());

                editTextStartDate.setText(selectedDateStr);
            });
        });

        EditText editTextEndDate = binding.editTextEndDate;
        editTextEndDate.setOnClickListener(v -> {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                String startDateTxt = binding.editTextStartDate.getText().toString();
                if (startDateTxt.equals("")) {
                    binding.editTextStartDate.setError("This field is required");
                    return;
                }
                Date startDate1 = simpleDateFormat.parse(startDateTxt);

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.YEAR, 1);
                constraintsBuilder.setStart(startDate.getTimeInMillis());
                constraintsBuilder.setEnd(endDate.getTimeInMillis());
                assert startDate1 != null;
                constraintsBuilder.setValidator(DateValidatorPointForward.from(startDate1.getTime()));
                CalendarConstraints constraints = constraintsBuilder.build();

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setCalendarConstraints(constraints);
                MaterialDatePicker<Long> datePicker = builder.build();
                datePicker.show(getChildFragmentManager(), "datePicker");

                datePicker.addOnPositiveButtonClickListener(selection -> {


                    Calendar selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());
                    selectedDate.setTimeInMillis(selection);

                    TimeZone deviceTimeZone = TimeZone.getTimeZone("UTC");
                    int offsetFromUTC = deviceTimeZone.getOffset(selection);
                    selectedDate.add(Calendar.MILLISECOND, offsetFromUTC);
                    selectedDate.add(Calendar.HOUR, 4);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

        categoryViewModel = new ViewModelProvider(new ViewModelStore(), new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), result -> {
            String[] newCategories = new String[categories.length + result.size()];

            for (int i = 0; i < result.size(); i++) {
                newCategories[i] = result.get(i).getName();
            }
            categories = newCategories;

            categoryDropDownAdapter = new ArrayAdapter<>(requireContext(), R.layout.categories_dropdown_items, categories);
            AutoCompleteTextView autoCompleteTextView = binding.autoCompleteTxt;
            autoCompleteTextView.setAdapter(categoryDropDownAdapter);

            autoCompleteTextView.setOnItemClickListener((adapterView, view1, position1, id) -> {
                String category = adapterView.getItemAtPosition(position1).toString();
                binding.autoCompleteTxt.setError(null);

                categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result1 -> categoryId = result1.getId());
            });
        });

        personalHabitType = binding.personalHabitType;
        personalHabitType.setOnClickListener(this::habitTypeSelection);
        publicHabitType = binding.publicHabitType;
        publicHabitType.setOnClickListener(this::habitTypeSelection);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reminderSwitch.setOnClickListener(this::activateReminder);
    }

    private void activateReminder(View view) {
        if (reminderSwitch.isChecked()) {
            binding.selectTimeBtn.setVisibility(View.VISIBLE);
        } else {
            binding.selectTimeBtn.setVisibility(View.GONE);
        }
    }


    private void habitTypeSelection(View view) {

        ColorStateList selectedTextColorBlack = ColorStateList.valueOf(getResources().getColor(R.color.black, getResources().newTheme()));
        ColorStateList selectedTextColorWhite = ColorStateList.valueOf(getResources().getColor(R.color.white, getResources().newTheme()));

        ColorStateList colorStateListDark = ColorStateList.valueOf(getResources().getColor(R.color.md_theme_light_primary, requireContext().getTheme()));

        if (view.getId() == R.id.personalHabitType) {
            personalHabitType.setBackgroundColor(requireContext().getColor(R.color.md_theme_light_primary));
            publicHabitType.setBackgroundColor(requireContext().getColor(R.color.md_theme_light_onPrimary));
            personalHabitType.setTextColor(selectedTextColorWhite);
            publicHabitType.setTextColor(selectedTextColorBlack);
            publicHabitType.setEnabled(true);
            publicHabitType.setStrokeColor(colorStateListDark);
            personalHabitType.setEnabled(false);
            habitType = HabitType.PERSONAL;

        } else {
            personalHabitType.setBackgroundColor(requireContext().getColor(R.color.md_theme_light_onPrimary));
            publicHabitType.setBackgroundColor(requireActivity().getColor(R.color.md_theme_light_primary));
            publicHabitType.setTextColor(selectedTextColorWhite);
            personalHabitType.setTextColor(selectedTextColorBlack);
            personalHabitType.setEnabled(true);
            personalHabitType.setStrokeColor(colorStateListDark);
            publicHabitType.setEnabled(false);
            habitType = HabitType.PUBLIC;
        }
    }

    private void createHabit(View view) {
        if (validateEmptyField()) return;

        Habit newHabit = new Habit();
        newHabit.setScore(0);
        newHabit.setUserId(mUser != null ? mUser.getUid() : "");
        newHabit.setName(binding.titleHabit.getText().toString());
        newHabit.setDescription(binding.description.getText() != null ? binding.description.getText().toString() : "");
        int duration = binding.durationText.getText().toString().equals("") ? 0 : Integer.parseInt(binding.durationText.getText().toString());
        newHabit.setDuration(duration);
        newHabit.setDurationUnit(durationUnit.name());
        newHabit.setCreationDate(new Date().getTime());
        int frequency = binding.frequencyText.getText().toString().equals("") ? 0 : Integer.parseInt(binding.frequencyText.getText().toString());
        newHabit.setFrequency(frequency);
        newHabit.setFrequencyUnit(frequencyUnit.name());

        newHabit.setHabitType(habitType.name());
        newHabit.setCategoryId(categoryId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

        if (newHabit.getImagePath() == null) {
            if (pathUri != null && !pathUri.equals("")) {
                newHabit.setImagePath(pathUri);
            } else {
                newHabit.setImagePath("default_image");
            }
        }
        habitViewModel.saveHabit(newHabit);

        if (newHabit.getHabitType().equalsIgnoreCase(HabitType.PUBLIC.toString())) {

            updateImageToCloud(downloadUri -> {
                if (downloadUri != null && !downloadUri.equals("")) {
                    newHabit.setImagePath(downloadUri);
                    habitViewModel.saveCloudHabit(newHabit);
                }
            });
        }


        if (reminderSwitch.isChecked()) {
            setAlarm();
        }
        Toast.makeText(requireContext(), "New habit registered", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack(R.id.createHabitFragment, true);
    }

    private void updateImageToCloud(OnTaskCompleted onTaskCompleted) {
        detailImage.setDrawingCacheEnabled(true);
        detailImage.buildDrawingCache(true);

        CompletableFuture.supplyAsync(() -> {
            if (detailImage.getDrawable() == null) {
                return null;
            }

            Bitmap bitmap = ((BitmapDrawable) detailImage.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] data = outputStream.toByteArray();

            StorageReference photosRef = storageRef.child("habit/" + currentUser.getUid() + "/" + UUID.randomUUID());
            UploadTask uploadTask = photosRef.putBytes(data);

            try {
                Tasks.await(uploadTask);
                return photosRef.getDownloadUrl();
            } catch (ExecutionException | InterruptedException e) {
                return null;
            }
        }).thenAcceptAsync(downloadUrl -> {
            if (downloadUrl != null) {
                onTaskCompleted.onImageUploaded(downloadUrl.toString());
            } else {
                Log.e(TAG, "Error fetching the photo URL");
            }
        }, Runnable::run);
    }

    //new Handler(Looper.getMainLooper())
    private boolean validateEmptyField() {
        boolean emptyField = false;

        if (binding.titleHabit.getText().toString().equals("")) {
            binding.titleHabit.setError("This field is required");
            emptyField = true;
        }

        if (binding.autoCompleteTxt.getText().toString().equals("")) {
            binding.autoCompleteTxt.setError("This field is required");
            emptyField = true;
        }

        if (binding.editTextEndDate.getText().toString().equals("")) {
            binding.editTextEndDate.setError("This field is required");
            emptyField = true;
        }

        if (binding.editTextStartDate.getText() != null && binding.editTextStartDate.getText().toString().equals("")) {
            binding.editTextStartDate.setError("This field is required");
            emptyField = true;
        }

        if (binding.frequencyText.getText().toString().equals("")) {
            binding.frequencyText.setError("This field is required");
            emptyField = true;
        }

        return emptyField;
    }

    private void setAlarm() {

        calendar = Calendar.getInstance();

        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        /*alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);*/

        Toast.makeText(requireContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }

    public void scheduleNotification(Context context, String message, int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        // Create a notification intent
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("message", message);

        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the current time and the specified time
        Calendar currentTime = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(year, month, dayOfMonth, hourOfDay, minute, 0);

        // Check if the scheduled time has already passed
        if (scheduledTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
            scheduledTime.setTimeInMillis(scheduledTime.getTimeInMillis() + 24 * 60 * 60 * 1000);
        }

        // Set the alarm using the AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime.getTimeInMillis(), pendingIntent);
    }

    private void showTimePicker(View view) {

        MaterialTimePicker selectReminderTime = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Reminder Time")
                .build();

        selectReminderTime.show(getParentFragmentManager(), "ReminderChannel");

        selectReminderTime.addOnPositiveButtonClickListener(v -> {

            if (selectReminderTime.getHour() > 12) {

                binding.selectTimeBtn.setText(
                        String.format("%02d", (selectReminderTime.getHour() - 12)) + " : " + String.format("%02d", selectReminderTime.getMinute()) + " PM"
                );

            } else {

                binding.selectTimeBtn.setText(selectReminderTime.getHour() + " : " + selectReminderTime.getMinute() + " AM");

            }

            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectReminderTime.getHour());
            calendar.set(Calendar.MINUTE, selectReminderTime.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

        });
    }

    private void notificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Habitude Reminder";
            String description = "Please complete your Habit";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ReminderChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    String picturePath = "content://media/" + result.getPath();
                    pathUri = picturePath;

                    Picasso.get().load(picturePath)
                            .resize(200, 300)
                            .centerInside()
                            .into(detailImage);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    private interface OnTaskCompleted {
        void onImageUploaded(String downloadUri);
    }
}
