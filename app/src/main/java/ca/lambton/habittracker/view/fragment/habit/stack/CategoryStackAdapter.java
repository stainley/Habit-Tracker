package ca.lambton.habittracker.view.fragment.habit.stack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.StackView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.view.fragment.habit.CollectionCardCategoryFragment;

public class CategoryStackAdapter extends RecyclerView.Adapter<CategoryStackAdapter.CategoryStackViewHolder> {

    private final List<CollectionCardCategoryFragment.CategoryStackModel> categoryStackModels;

    public CategoryStackAdapter(List<CollectionCardCategoryFragment.CategoryStackModel> categoryStackModels) {
        this.categoryStackModels = categoryStackModels;
    }

    @NonNull
    public CategoryStackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_stack_category_item, parent, false);
        return new CategoryStackViewHolder(view);
    }


    public void onBindViewHolder(@NonNull CategoryStackViewHolder holder, int position) {
        CollectionCardCategoryFragment.CategoryStackModel cardModel = categoryStackModels.get(position);
        holder.bind(cardModel, position);
    }

    @Override
    public int getItemCount() {
        return categoryStackModels.size();
    }

    static class CategoryStackViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final ImageView categoryHabitImage;

        public CategoryStackViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.category_card);
            categoryHabitImage = itemView.findViewById(R.id.category_habit_image);
        }

        public void bind(CollectionCardCategoryFragment.CategoryStackModel card, int position) {

            categoryHabitImage.setImageDrawable(card.getImage());
        }
    }


}
