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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentCardStackCollectionBinding;
import ca.lambton.habittracker.view.fragment.habit.stack.CategoryStackAdapter;
import ca.lambton.habittracker.view.fragment.habit.stack.StackLayoutManager;

public class CollectionCardCategoryFragment extends Fragment {

    FragmentCardStackCollectionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardStackCollectionBinding.inflate(inflater);
        RecyclerView cardStackCollectionRv = binding.cardStackCollectionRv;


        List<CategoryStackModel> categoryStackModels = new ArrayList<>();
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.yoga)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.reading_book)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.exercise)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.stretching)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.wakeup_early)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.yoga)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.reading_book)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.exercise)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.stretching)));
        categoryStackModels.add(new CategoryStackModel(AppCompatResources.getDrawable(requireContext(), R.drawable.wakeup_early)));

        CategoryStackAdapter categoryStackAdapter = new CategoryStackAdapter(categoryStackModels);


        StackLayoutManager stackLayoutManager = new StackLayoutManager();
        stackLayoutManager.setHorizontalLayout(true);
        stackLayoutManager.snap();
        stackLayoutManager.getCenterFirstItem();

        cardStackCollectionRv.setLayoutManager(stackLayoutManager);

        cardStackCollectionRv.setAdapter(categoryStackAdapter);

        return binding.getRoot();
    }


    public static class CategoryStackModel {
        private final Drawable image;
        private MaterialCardView materialCardView;

        public CategoryStackModel(Drawable image) {
            this.image = image;
        }

        public Drawable getImage() {
            return image;
        }

        public MaterialCardView getMaterialCardView() {
            return materialCardView;
        }

        public void setMaterialCardView(MaterialCardView materialCardView) {
            this.materialCardView = materialCardView;
        }
    }
}
