package ca.lambton.habittracker.view.fragment.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentRecycleViewCarouselBinding;

public class HabitCardFragment extends Fragment {

    FragmentRecycleViewCarouselBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<HabitCard> habitCardList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecycleViewCarouselBinding.inflate(inflater);

        RecyclerView recycleHabitCarousel = binding.recycleCarousel;
        recycleHabitCarousel.setLayoutManager(new GridLayoutManager(requireContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        HabitCardAdapter habitCardAdapter = new HabitCardAdapter(habitCardList);

        categoryViewModel = new ViewModelProvider(requireActivity(), new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(requireActivity(), categories -> {
            categories.forEach(category -> {
                habitCardList.add(new HabitCard(category.getName(), requireContext().getResources().getIdentifier(category.getImageName(), "drawable", requireContext().getPackageName())));
            });
            habitCardAdapter.notifyItemChanged(0, categories.size());
        });


        recycleHabitCarousel.setAdapter(habitCardAdapter);

        return binding.getRoot();
    }

}
