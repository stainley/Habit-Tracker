package ca.lambton.habittracker.view.fragment.habit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentRecycleViewCarouselBinding;

public class HabitCardFragment extends Fragment {

    FragmentRecycleViewCarouselBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecycleViewCarouselBinding.inflate(inflater);

        RecyclerView recycleHabitCarousel = binding.recycleCarousel;
        recycleHabitCarousel.setLayoutManager(new GridLayoutManager(requireContext(), 1, LinearLayoutManager.HORIZONTAL, false));

        List<HabitCard> habitCardList = new ArrayList<>();
        habitCardList.add(new HabitCard("Running", AppCompatResources.getDrawable(requireContext(), R.drawable.running_img)));
        habitCardList.add(new HabitCard("Wake up early", AppCompatResources.getDrawable(requireContext(), R.drawable.wakeup_early)));
        habitCardList.add(new HabitCard("Exercise", AppCompatResources.getDrawable(requireContext(), R.drawable.exercise)));
        habitCardList.add(new HabitCard("Yoga", AppCompatResources.getDrawable(requireContext(), R.drawable.yoga)));
        habitCardList.add(new HabitCard("Stretching", AppCompatResources.getDrawable(requireContext(), R.drawable.stretching)));
        habitCardList.add(new HabitCard("Reading book", AppCompatResources.getDrawable(requireContext(), R.drawable.reading_book)));


        HabitCardAdapter habitCardAdapter = new HabitCardAdapter(habitCardList);
        recycleHabitCarousel.setAdapter(habitCardAdapter);

        return binding.getRoot();
    }


    static class HabitCard {
        private final String habitName;
        private Drawable habitPicture;

        public HabitCard(String habitName, Drawable habitPicture) {
            this.habitName = habitName;
            this.habitPicture = habitPicture;
        }

        public String getHabitName() {
            return habitName;
        }

        public Drawable getHabitPicture() {
            return habitPicture;
        }
    }
}
