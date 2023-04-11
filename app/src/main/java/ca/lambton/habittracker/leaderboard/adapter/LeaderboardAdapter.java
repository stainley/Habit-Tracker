package ca.lambton.habittracker.leaderboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.leaderboard.model.Leaderboard;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<Leaderboard> leaderboardList;

    public LeaderboardAdapter(List<Leaderboard> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_row, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.positionText.setText(String.valueOf(position + 1));
        holder.nameTex.setText(leaderboardList.get(position).getName());
        holder.scoreText.setText(String.valueOf(leaderboardList.get(position).getScore()));

        if (leaderboardList.get(position).getImageUrl() != null && !leaderboardList.get(position).getImageUrl().equals("")) {
            Picasso.get().load(leaderboardList.get(position).getImageUrl())
                    .fit()
                    .into(holder.photoView);
        }
    }

    @Override
    public int getItemCount() {
        return this.leaderboardList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView photoView;
        private final TextView nameTex;
        private final TextView scoreText;
        private final TextView positionText;


        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.profile_picture);
            nameTex = itemView.findViewById(R.id.name_label);
            scoreText = itemView.findViewById(R.id.score_label);
            positionText = itemView.findViewById(R.id.row_position);
        }
    }

}
