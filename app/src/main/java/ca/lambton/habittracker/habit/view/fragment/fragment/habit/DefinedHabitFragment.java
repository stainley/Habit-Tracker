package ca.lambton.habittracker.habit.view.fragment.fragment.habit;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.perf.FirebasePerformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentDefinedHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.view.fragment.fragment.habit.description.HabitCategoryDescriptionFragment;

public class DefinedHabitFragment extends Fragment {
    private static final String TAG = DefinedHabitFragment.class.getName();
    private final List<HabitCard> habitCards = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private FragmentDefinedHabitsBinding binding;
    private ViewPager2 habitsPager;
    protected CategoryViewModel categoryViewModel;
    private PredifinedHabitAdapter predifinedHabitAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentDefinedHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitsPager = binding.habitsPager;
        configureAnimationPager();
        String packageName = requireContext().getPackageName();
        categoryViewModel = new ViewModelProvider(requireActivity(), new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

        predifinedHabitAdapter = new PredifinedHabitAdapter(habitCards);

        //TODO: refactor this code
        categoryViewModel.getAllCategories().observe(requireActivity(), categories -> {
            this.categories.addAll(categories);

            categories.forEach(category -> {
                habitCards.add(new HabitCard(category.getName(), requireContext().getResources().getIdentifier(category.getImageName(), "drawable", packageName)));
            });
            predifinedHabitAdapter.notifyItemChanged(0, categories.size());
        });

        habitsPager.setAdapter(predifinedHabitAdapter);

        habitsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                onPageSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.i(TAG, "onPageScrollStateChanged: " + state);

            }
        });


    }

    private void onPageSelect(int position) {
        FirebasePerformance firebasePerformance = FirebasePerformance.getInstance();
        firebasePerformance.setPerformanceCollectionEnabled(true);

        firebasePerformance.newTrace("Category Carrousel: start");

        Log.i(TAG, "onPageSelected: " + position);

        HabitCategoryDescriptionFragment habitCategoryDescriptionFragment = HabitCategoryDescriptionFragment.newInstance(categories.get(position));
        FragmentManager parentFragmentManager = getParentFragmentManager();
        parentFragmentManager.beginTransaction().replace(R.id.habit_category_desc_fragment, habitCategoryDescriptionFragment).addToBackStack(null).commit();

        firebasePerformance.newTrace("Category Carrousel: end");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            int position = getArguments().getInt("categoryPosition");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                onPageSelect(position);
                //HabitCategoryDescriptionFragment habitCategoryDescriptionFragment = HabitCategoryDescriptionFragment.newInstance(categories.get(position));
                //FragmentManager parentFragmentManager = getParentFragmentManager();
                //parentFragmentManager.beginTransaction().replace(R.id.habit_category_desc_fragment, habitCategoryDescriptionFragment).addToBackStack(null).commit();

            }).start();

        }

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
        habitsPager.addItemDecoration(new HorizontalMarginItemDecoration(requireContext(), R.dimen.viewpager_current_item_horizontal_margin));
    }

    static class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalMarginInPx;

        public HorizontalMarginItemDecoration(@NonNull Context context, @DimenRes int horizontalMarginInDp) {
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
