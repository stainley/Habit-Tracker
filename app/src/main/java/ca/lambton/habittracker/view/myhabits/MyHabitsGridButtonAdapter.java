package ca.lambton.habittracker.view.myhabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import ca.lambton.habittracker.R;

public class MyHabitsGridButtonAdapter extends ArrayAdapter<MyHabitsGridButton> {

    public MyHabitsGridButtonAdapter(@NonNull Context context, ArrayList<MyHabitsGridButton> myHabitsGridButtonModelArrayList) {
        super(context, 0, myHabitsGridButtonModelArrayList);
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
        TextView courseTV = listitemView.findViewById(R.id.myHabitButtonLabel);
        ImageView courseIV = listitemView.findViewById(R.id.myHabitButtonIcon);

        courseTV.setText(myHabitsGridButtonModel.getButtonLabel());
        courseIV.setImageResource(myHabitsGridButtonModel.getIconId());
        return listitemView;
    }
}
