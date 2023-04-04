package ca.lambton.habittracker.view.newhabit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
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
import com.google.firebase.auth.UserProfileChangeRequest;
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

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Duration;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.HabitType;
import jp.wasabeef.picasso.transformations.CropTransformation;

public class CreateHabitFragment extends Fragment {
    private static final String TAG = CreateHabitFragment.class.getName();
    private HabitViewModel habitViewModel;
    private CategoryViewModel categoryViewModel;
    FragmentCreateHabitLayoutBinding binding;
    private Duration durationUnit = Duration.MINUTES;
    private HabitType habitType = HabitType.PERSONAL;
    private Frequency frequencyUnit = Frequency.DAILY;
    private Uri tempImageUri = null;
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

        Toast.makeText(requireContext(), "New habit registered", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack(R.id.createHabitFragment, true);
    }

    private void updateImageToCloud(OnTaskCompleted onTaskCompleted) {

        detailImage.setDrawingCacheEnabled(true);
        detailImage.buildDrawingCache(true);
        new Thread(() -> {

            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (detailImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) detailImage.getDrawable()).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] data = outputStream.toByteArray();

                StorageReference photosRef = storageRef.child("habit/" + currentUser.getUid() + "/" + UUID.randomUUID());
                UploadTask uploadTask = photosRef.putBytes(data);

                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    //Toast.makeText(getActivity(), "Photo error uploading occurred", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                });


                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return photosRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri photoDownloadUri = task.getResult();
                        onTaskCompleted.onImageUploaded(photoDownloadUri.toString());
                    } else {
                        // Handle failures
                        Log.e(TAG, "Error fetching the photo URL");
                    }
                });
            }
        }).start();
    }

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

        if (binding.editTextStartDate.getText().toString().equals("")) {
            binding.editTextStartDate.setError("This field is required");
            emptyField = true;
        }

        if (binding.frequencyText.getText().toString().equals("")) {
            binding.frequencyText.setError("This field is required");
            emptyField = true;
        }

        return emptyField;
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    tempImageUri = result;
                    String picturePath = "content://media/" + result.getPath();
                    pathUri = picturePath;

                    Picasso.get().load(picturePath)
                            .resize(200,300)
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
