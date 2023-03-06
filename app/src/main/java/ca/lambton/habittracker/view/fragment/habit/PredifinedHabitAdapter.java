package ca.lambton.habittracker.view.fragment.habit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

import ca.lambton.habittracker.R;

public class PredifinedHabitAdapter extends RecyclerView.Adapter<PredifinedHabitAdapter.HabitViewHolder> {

    private final List<DefinedHabitFragment.HabitDetail> habitDetails;

    public PredifinedHabitAdapter(List<DefinedHabitFragment.HabitDetail> habitDetails) {
        this.habitDetails = habitDetails;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_stack_category_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        holder.imageView.setImageDrawable(habitDetails.get(position).getImage());
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
