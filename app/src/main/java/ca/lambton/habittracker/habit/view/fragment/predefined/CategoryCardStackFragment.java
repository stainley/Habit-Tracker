package ca.lambton.habittracker.habit.view.fragment.predefined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;

public class CategoryCardStackFragment extends Fragment {



    public CategoryCardStackFragment() {
    }

    public static CategoryCardStackFragment newInstance(Category category) {
        CategoryCardStackFragment fragment = new CategoryCardStackFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.card_stack_category_item, container, false);


        ImageView categoryHabitImage = view.findViewById(R.id.category_habit_image);
        if (getArguments() != null) {
            String packageName = requireContext().getPackageName();

            Category category = (Category) getArguments().getSerializable("category");

            int drawable = requireContext().getResources().getIdentifier(category.getImageName(), "drawable", packageName);

            Picasso.get()
                    .load(drawable)
                    .into(categoryHabitImage);
        }


        return view;
    }
}
