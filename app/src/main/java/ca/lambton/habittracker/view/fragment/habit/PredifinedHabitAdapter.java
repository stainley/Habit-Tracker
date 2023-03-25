package ca.lambton.habittracker.view.fragment.habit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.R;

public class PredifinedHabitAdapter extends RecyclerView.Adapter<PredifinedHabitAdapter.HabitViewHolder> {

    private final List<HabitCard> habitDetails;
    private Context context;

    public PredifinedHabitAdapter(List<HabitCard> habitDetails) {
        this.habitDetails = habitDetails;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_stack_category_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {

        int drawable = habitDetails.get(position).getHabitPicture();
        if (drawable > 0) {
            Picasso.get().load(drawable).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return habitDetails.size();
    }

    static final protected class HabitViewHolder extends ViewHolder {

        private final ImageView imageView;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_habit_image);
        }
    }
}
