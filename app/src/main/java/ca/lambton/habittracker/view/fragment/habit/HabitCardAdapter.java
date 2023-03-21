package ca.lambton.habittracker.view.fragment.habit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.R;

public class HabitCardAdapter extends RecyclerView.Adapter<HabitCardAdapter.HabitCardViewHolder> {

    private final List<HabitCard> habitCards;

    public HabitCardAdapter(List<HabitCard> habitCards) {
        this.habitCards = habitCards;
    }

    @NonNull
    @Override
    public HabitCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_habit, parent, false);
        return new HabitCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitCardViewHolder holder, int position) {
        holder.habitName.setText(habitCards.get(position).getHabitName());
        int drawable = habitCards.get(position).getHabitPicture();
        if (drawable > 0) {
            Picasso.get().load(drawable).into(holder.habitPicture);
        }
    }

    @Override
    public int getItemCount() {
        return habitCards.size();
    }

    static class HabitCardViewHolder extends RecyclerView.ViewHolder {

        private final ImageView habitPicture;
        private final TextView habitName;

        public HabitCardViewHolder(@NonNull View itemView) {
            super(itemView);
            habitPicture = itemView.findViewById(R.id.habit_image);
            habitName = itemView.findViewById(R.id.habit_name);
        }
    }
}
