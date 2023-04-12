package ca.lambton.habittracker.leaderboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.leaderboard.model.Leaderboard;
import ca.lambton.habittracker.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<Leaderboard> leaderboardList;
    private final OnCurrentUserPositionListener positionListener;

    public LeaderboardAdapter(List<Leaderboard> leaderboardList, OnCurrentUserPositionListener positionListener) {
        this.leaderboardList = leaderboardList;
        this.positionListener = positionListener;
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
        String scoresInK = Utils.formatNumberToK(leaderboardList.get(position).getScore());
        holder.scoreText.setText(scoresInK);

        if (leaderboardList.get(position).getImageUrl() != null && !leaderboardList.get(position).getImageUrl().equals("")) {
            Picasso.get().load(leaderboardList.get(position).getImageUrl())
                    .placeholder(R.drawable.ic_profile_custom)
                    .fit()
                    .into(holder.photoView);
        }

        positionListener.onUserPosition(holder.cardView, position);
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
        private final CardView cardView;


        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.profile_picture);
            nameTex = itemView.findViewById(R.id.name_label);
            scoreText = itemView.findViewById(R.id.score_label);
            positionText = itemView.findViewById(R.id.row_position);
            cardView = itemView.findViewById(R.id.card_leaderboard);
        }
    }

    public interface OnCurrentUserPositionListener {
        void onUserPosition(CardView cardView, int position);
    }
}
