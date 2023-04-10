package ca.lambton.habittracker.habit.view.myhabits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jetbrains.annotations.Contract;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private final OnCategoryCallback onCategoryCallback;
    private final List<Category> categories;
    private static final int CORNER_RADIUS = 16;


    public CategoryRecycleAdapter(List<Category> categories, OnCategoryCallback onCallback) {
        this.categories = categories;
        this.onCategoryCallback = onCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        setCategoryDuration(holder.categoryDuration, category.getDuration());
        setCategoryImage(holder.categoryImage, category.getImageName());
        holder.categoryName.setText(category.getName());
        holder.categoryInterval.setText(category.getInterval());
        holder.categoryCard.setOnClickListener(view -> onCategoryCallback.onRowClicked(position));
    }

    private void setCategoryDuration(TextView categoryDurationView, int duration) {
        String[] categoryPeriod = categoryDurationView.getContext().getResources().getStringArray(R.array.category_period);
        String durationString = "";
        if (duration >= 1 && duration <= categoryPeriod.length) {
            durationString = categoryPeriod[duration - 1];
        }
        categoryDurationView.setText(durationString);
    }

    @NonNull
    @Contract(value = " -> new", pure = true)
    private static Transformation getRoundedTransformation() {
        return new RoundedCornersTransformation(CORNER_RADIUS, 0, RoundedCornersTransformation.CornerType.ALL);
    }

    private void setCategoryImage(ImageView categoryImageView, String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            categoryImageView.setImageResource(R.drawable.placeholder_image);
            return;
        }

        int imageResourceId = categoryImageView.getResources().getIdentifier(imageName, "drawable", categoryImageView.getContext().getPackageName());

        if (imageResourceId == 0) {
            categoryImageView.setImageResource(R.drawable.placeholder_image);
            return;
        }

        Picasso.get()
                .load(imageResourceId)
                .fit()
                .transform(getRoundedTransformation())
                .error(R.drawable.placeholder_image)
                .into(categoryImageView);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

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
