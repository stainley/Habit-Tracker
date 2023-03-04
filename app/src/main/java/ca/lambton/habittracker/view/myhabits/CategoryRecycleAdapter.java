package ca.lambton.habittracker.view.myhabits;

//import android.view.LayoutInflater;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class CategoryRecycleAdapter {
//}
//
//package ca.app.assasins.taskappsassassinsandroid.category.view.adpter;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.lambton.habittracker.R;

//import ca.app.assasins.taskappsassassinsandroid.R;
//import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private final OnCategoryCallback onCategoryCallback;
    private final List<Category> categories;


    public CategoryRecycleAdapter(List<Category> categories, OnCategoryCallback onCallback) {
        this.categories = categories;
        this.onCategoryCallback = onCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_category_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (categories.get(position).getDuration()) {
            case 3:
                holder.categoryDuration.setText("Daily / Weekly /  Monthly");
                break;
            case 2:
                holder.categoryDuration.setText("Daily / Weekly");
                break;
            case 1:
                holder.categoryDuration.setText("Daily");
                break;
            default:
                holder.categoryDuration.setText("");
                break;
        }

        holder.categoryImage.setImageResource(categories.get(position).getIconId());
        holder.categoryName.setText(categories.get(position).getName());
        holder.categoryInterval.setText(categories.get(position).getInterval());
        holder.categoryCard.setOnClickListener(view -> {
            onCategoryCallback.onRowClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView categoryImage;
        private final TextView categoryName;
        private final TextView categoryDuration;
        private final TextView categoryInterval;
        private final CardView categoryCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryNameLabel);
            categoryDuration = itemView.findViewById(R.id.categoryDurationLabel);
            categoryInterval = itemView.findViewById(R.id.categoryIntervalLabel);
            categoryCard = itemView.findViewById(R.id.categoryCard);
        }
    }

    public interface OnCategoryCallback {
        void onRowClicked(int position);
    }
}
