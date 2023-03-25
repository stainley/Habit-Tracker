package ca.lambton.habittracker.view.ongoingHabits;

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

public class AchievementGridAdapter extends ArrayAdapter<AchievementInfo> {

    public AchievementGridAdapter(@NonNull Context context, ArrayList<AchievementInfo> achievementModelArrayList) {
        super(context, 0, achievementModelArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_achievement, parent, false);
        }

        AchievementInfo achievementInfo = getItem(position);
        TextView achievementLabel = listitemView.findViewById(R.id.achievementLabel);
        MaterialCardView achievementsCard = listitemView.findViewById(R.id.achievementsCard);
        ImageView scoreImageView = listitemView.findViewById(R.id.scoreImageView);
        ImageView starImageView = listitemView.findViewById(R.id.starImageView);

        achievementLabel.setText(achievementInfo.getAchievementLabel());
        scoreImageView.setImageResource(achievementInfo.getScoreImage());
        starImageView.setImageResource(achievementInfo.getStarImage());
        
        return listitemView;
    }
}