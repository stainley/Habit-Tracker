package ca.lambton.habittracker.habit.view.myhabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ca.lambton.habittracker.R;

public class MyHabitsGridButtonAdapter extends ArrayAdapter<MyHabitsGridButton> {
    private OnMyHabitsGridButtonCallback onMyHabitsGridButtonCallback;

    public MyHabitsGridButtonAdapter(@NonNull Context context, ArrayList<MyHabitsGridButton> myHabitsGridButtonModelArrayList,
                                     MyHabitsGridButtonAdapter.OnMyHabitsGridButtonCallback onCallback) {
        super(context, 0, myHabitsGridButtonModelArrayList);

        this.onMyHabitsGridButtonCallback = onCallback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item_my_habbits_buttons, parent, false);
        }

        MyHabitsGridButton myHabitsGridButtonModel = getItem(position);
        TextView myHabitButtonLabel = listitemView.findViewById(R.id.myHabitButtonLabel);
        ImageView myHabitButtonIcon = listitemView.findViewById(R.id.myHabitButtonIcon);
        MaterialCardView myHabitButtonCard = listitemView.findViewById(R.id.myHabitButtonCard);

        myHabitButtonLabel.setText(myHabitsGridButtonModel.getButtonLabel());
        myHabitButtonIcon.setImageResource(myHabitsGridButtonModel.getIconId());
        myHabitButtonCard.setOnClickListener(view -> {
            onMyHabitsGridButtonCallback.onRowClicked(position);
        });

        return listitemView;
    }

    public interface OnMyHabitsGridButtonCallback {
        void onRowClicked(int position);
    }
}
