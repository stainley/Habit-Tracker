package ca.lambton.habittracker.view.ongoingHabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ca.lambton.habittracker.R;

public class OngoingHabitDetailGridInfoAdapter extends ArrayAdapter<OngoingHabitDetailGridInfo> {

    public OngoingHabitDetailGridInfoAdapter(@NonNull Context context, ArrayList<OngoingHabitDetailGridInfo> myHabitsGridButtonModelArrayList) {
        super(context, 0, myHabitsGridButtonModelArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_ongoing_habit_detail_progress, parent, false);
        }

        OngoingHabitDetailGridInfo ongoingHabitDetailGridInfo = getItem(position);
        TextView cardLabel = listitemView.findViewById(R.id.cardLabel);
        TextView progressLabel = listitemView.findViewById(R.id.progressLabel);
        MaterialCardView ongoingHabitDetailGridViewCard = listitemView.findViewById(R.id.ongoingHabitDetailGridViewCard);

        cardLabel.setText(ongoingHabitDetailGridInfo.getCardLabel());
        progressLabel.setText(ongoingHabitDetailGridInfo.getProgressText());

        return listitemView;
    }
}
