package ca.lambton.habittracker.habit.view.fragment.fragment.habit.description;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ca.lambton.habittracker.R;

public class CategoryButtonRVAdapter extends RecyclerView.Adapter<CategoryButtonRVAdapter.ButtonViewHolder> {
    private final List<String> foodButtons;
    private final OnButtonPressedCallback onButtonPressedCallback;

    public CategoryButtonRVAdapter(List<String> foodButtons, OnButtonPressedCallback onButtonPressedCallback) {
        this.foodButtons = foodButtons;
        this.onButtonPressedCallback = onButtonPressedCallback;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.predefined_category_buttons, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        holder.nameButton.setText(foodButtons.get(position));

        this.onButtonPressedCallback.onCardPressed(holder.cardView, position);
    }

    @Override
    public int getItemCount() {
        return foodButtons.size();
    }

    protected static final class ButtonViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameButton;
        private final MaterialCardView cardView;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameButton = itemView.findViewById(R.id.button_name_text);
            cardView = itemView.findViewById(R.id.habit_category_button);
        }
    }

    public interface OnButtonPressedCallback {
        void onCardPressed(MaterialCardView view, int position);
    }
}
