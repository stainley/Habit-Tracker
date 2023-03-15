package ca.lambton.habittracker.view.fragment.habit.first;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentFirstHabitBinding;
import ca.lambton.habittracker.view.fragment.habit.HabitCard;
import ca.lambton.habittracker.view.fragment.habit.PredifinedHabitAdapter;

public class FirstHabitFragment extends Fragment {

    FragmentFirstHabitBinding binding;
    private ViewPager2 habitsPager;

    private CategoryViewModel categoryViewModel;
    private final List<HabitCard> habitDetails = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentFirstHabitBinding.inflate(LayoutInflater.from(requireContext()));
        habitsPager = binding.habitsPager;
        configureAnimationPager();
        PredifinedHabitAdapter predifinedHabitAdapter = new PredifinedHabitAdapter(habitDetails);

        categoryViewModel = new ViewModelProvider(requireActivity(), new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(requireActivity(), categories -> {
            categories.forEach(category -> {
                habitDetails.add(new HabitCard(category.getName(), requireContext().getResources().getIdentifier(category.getImageName(), "drawable", requireContext().getPackageName())));
            });

            predifinedHabitAdapter.notifyItemChanged(0, categories.size());
        });

        habitsPager.setAdapter(predifinedHabitAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        habitsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.categoryName.setText(habitDetails.get(position).getHabitName());
            }
        });

        return binding.getRoot();
    }

    private void configureAnimationPager() {
        habitsPager.setOffscreenPageLimit(1);
        float nextItemVisiblePx = getResources().getDimension(R.dimen.viewpager_next_item_visible);
        float currentItemHorizontalMarginPx = getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setTranslationX(-pageTranslationX * position);
            page.setScaleY(1 - (0.25f * Math.abs(position)));
            page.setAlpha(0.25f + (1 - Math.abs(position)));
        };

        habitsPager.setPageTransformer(pageTransformer);
        habitsPager.addItemDecoration(new FirstHabitFragment.HorizontalMarginItemDecoration(requireContext(), R.dimen.viewpager_current_item_horizontal_margin));
    }

    static class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalMarginInPx;

        public HorizontalMarginItemDecoration(Context context, @DimenRes int horizontalMarginInDp) {
            horizontalMarginInPx = (int) context.getResources().getDimension(horizontalMarginInDp);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = horizontalMarginInPx;
            outRect.left = horizontalMarginInPx;
        }
    }
}
