package ca.lambton.habittracker.community.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.community.model.PostComment;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private final List<PostComment> posts;

    public CommunityAdapter(List<PostComment> posts) {
        this.posts = posts;
        System.out.println("Adapter: " + posts.size());
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        holder.authorText.setText(posts.get(position).post.getUser().getName());
        holder.postText.setText(posts.get(position).post.getMessage());

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(posts.get(position).post.getCreationDate(), inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(outputFormatter);
        holder.dayPostedTxt.setText(formattedDateTime);

    }

    @Override
    public int getItemCount() {
        System.out.println("Adapter: " + posts.size());
        return posts.size();
    }

    static final class CommunityViewHolder extends RecyclerView.ViewHolder {

        private final TextView authorText;
        private final TextView postText;
        private final TextView dayPostedTxt;


        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);

            authorText = itemView.findViewById(R.id.author_text);
            postText = itemView.findViewById(R.id.post_text);
            dayPostedTxt = itemView.findViewById(R.id.day_posted);
        }

    }
}
