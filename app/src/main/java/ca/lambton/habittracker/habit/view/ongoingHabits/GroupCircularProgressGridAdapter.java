package ca.lambton.habittracker.habit.view.ongoingHabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import ca.lambton.habittracker.R;

public class GroupCircularProgressGridAdapter extends ArrayAdapter<String> {

    public GroupCircularProgressGridAdapter(@NonNull Context context, ArrayList<String> progressArrayList) {
        super(context, 0, progressArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_group_circular_progress, parent, false);
        }

        String groupProgress = getItem(position);
        TextView habitPercentageNumText = listitemView.findViewById(R.id.habitPercentageNumText);
        CircularProgressIndicator groupCircularProgressValue = (CircularProgressIndicator) listitemView.findViewById(R.id.habitProgressbar);

        habitPercentageNumText.setText(groupProgress + "%" );
        groupCircularProgressValue.setProgress(Integer.parseInt(groupProgress));

        return listitemView;
    }
}