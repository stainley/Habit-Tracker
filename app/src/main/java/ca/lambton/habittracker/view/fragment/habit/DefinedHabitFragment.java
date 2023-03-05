package ca.lambton.habittracker.view.fragment.habit;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentDefinedHabitsBinding;

public class DefinedHabitFragment extends Fragment {
    private static final String TAG = DefinedHabitFragment.class.getName();

    FragmentDefinedHabitsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentDefinedHabitsBinding.inflate(inflater, container, false);

        ViewPager2 habitsPager = binding.habitsPager;
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

        List<HabitDetail> habitDetails = new ArrayList<>();
        habitDetails.add(new HabitDetail(AppCompatResources.getDrawable(requireContext(), R.drawable.running_img), "Running"));
        habitDetails.add(new HabitDetail(AppCompatResources.getDrawable(requireContext(), R.drawable.exercise), "Exercise"));
        habitDetails.add(new HabitDetail(AppCompatResources.getDrawable(requireContext(), R.drawable.yoga), "Yoga"));
        habitDetails.add(new HabitDetail(AppCompatResources.getDrawable(requireContext(), R.drawable.stretching), "Stretching"));
        habitDetails.add(new HabitDetail(AppCompatResources.getDrawable(requireContext(), R.drawable.reading_book), "Reading book"));

        PredifinedHabitAdapter predifinedHabitAdapter = new PredifinedHabitAdapter(habitDetails);
        habitsPager.setAdapter(predifinedHabitAdapter);

        habitsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.i(TAG, "onPageSelected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.i(TAG, "onPageScrollStateChanged: " + state);
            }
        });

        return binding.getRoot();
    }

    public static final class HabitDetail {
        private Drawable image;
        private String label;

        public HabitDetail(Drawable image, String label) {
            this.image = image;
            this.label = label;
        }

        public Drawable getImage() {
            return image;
        }

        public String getLabel() {
            return label;
        }
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
