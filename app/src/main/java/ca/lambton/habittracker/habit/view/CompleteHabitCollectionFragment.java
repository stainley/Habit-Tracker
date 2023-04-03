package ca.lambton.habittracker.habit.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentCompleteHabitCollectionBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.view.adapter.CompleteHabitAdapter;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteHabitCollectionFragment extends Fragment {

    private FragmentCompleteHabitCollectionBinding binding;
    private RecyclerView recycleCompleteHabit;
    private HabitViewModel habitViewModel;
    private CompleteHabitAdapter completeHabitAdapter;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();
    private String packageName = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCompleteHabitCollectionBinding.inflate(LayoutInflater.from(requireContext()));

        recycleCompleteHabit = binding.recycleCompleteHabit;
        recycleCompleteHabit.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);
        completeHabitAdapter = new CompleteHabitAdapter(habitProgresses, getOnCompleteListener());

        recycleCompleteHabit.setAdapter(completeHabitAdapter);
        packageName = requireContext().getPackageName();
    }

    @NonNull
    private CompleteHabitAdapter.OnCompleteListener getOnCompleteListener() {
        return (circleImageView, completeHabitCard, position) -> {
            String imagePath = habitProgresses.get(position).getHabit().getImagePath();
            if (imagePath.equals("") || imagePath.equals("default_image")) {
                Picasso.get().load(R.drawable.default_image).fit().into(circleImageView);
            } else {
                //TODO: change this implementation don't obtain the image by name on drawable
                int imagePredefine = getResources().getIdentifier(imagePath, "drawable", packageName);
                if (imagePredefine > 0) {
                    Picasso.get().load(imagePredefine).fit().into(circleImageView);
                } else {
                    Picasso.get().load(imagePath).fit().into(circleImageView);
                }
            }

            completeHabitCard.setOnClickListener(view -> {
                NavDirections navDirections = CompleteHabitCollectionFragmentDirections.actionCompleteHabitCollectionFragmentToCompleteHabitFragment().setHabitProgress(habitProgresses.get(position));
                Navigation.findNavController(requireView()).navigate(navDirections);
            });
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.habitProgresses.clear();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        //TODO: for test  remove the below. remove before release
        calendar.add(Calendar.WEEK_OF_MONTH, 1);


        habitViewModel.getAllProgress().observe(this, habitProgressResult -> {

            List<HabitProgress> habitPassDueDate = habitProgressResult.stream()
                    .filter(habitProgress ->
                            calendar.getTime().after(new Date(habitProgress.getHabit().getEndDate())))
                    .collect(Collectors.toList());

            habitProgresses.addAll(habitPassDueDate);
            completeHabitAdapter.notifyItemRangeChanged(0, habitProgressResult.size());
        });

    }
}
