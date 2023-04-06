package ca.lambton.habittracker.community.view.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.habit.model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private final List<Post> posts;

    private final OnCommunityListener communityListener;
    private Context context;

    public CommunityAdapter(List<Post> posts, OnCommunityListener communityListener) {
        this.posts = posts;
        this.communityListener = communityListener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_community, parent, false);
        context = parent.getContext();

        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        holder.authorText.setText(posts.get(position).getUser().getName());
        holder.postText.setText(posts.get(position).getMessage());


        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(posts.get(position).getCreationDate(), inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(outputFormatter);
        holder.dayPostedTxt.setText(formattedDateTime);

        User user = posts.get(position).getUser();
        if (user != null) {
            String photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                if (!photoUrl.equals("")) {
                    Picasso.get()
                            .load(photoUrl)
                            .fit().into(holder.profilePhoto);
                }
            }
        }

        communityListener.onMoreOptionCallback(holder.moreOptionButton, position);
        communityListener.onMoreOptionCallback(holder.postCardView, position);


        if (!posts.get(position).getPostImage().getPath().equals("")) {
            holder.postImage.setVisibility(View.VISIBLE);
            holder.postImage.setClipToOutline(true);
            //holder.postPictureFrame.setClipToOutline(true);
            Picasso.get().load(posts.get(position).getPostImage().getPath())
                    .transform(new CropTransformation(0.7f, 0.8f, CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                    .transform(new RoundedCornersTransformation(24, 24, RoundedCornersTransformation.CornerType.ALL))
                    //.resize(400,300)
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

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
        private final CircleImageView profilePhoto;
        private final ImageButton moreOptionButton;
        private final ImageView postImage;
        private final CardView postCardView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);

            authorText = itemView.findViewById(R.id.author_text);
            postText = itemView.findViewById(R.id.post_text);
            dayPostedTxt = itemView.findViewById(R.id.day_posted);
            profilePhoto = itemView.findViewById(R.id.profile_picture);
            moreOptionButton = itemView.findViewById(R.id.more_options_button);

            postImage = itemView.findViewById(R.id.post_picture);
            postCardView = postImage.findViewById(R.id.post_card);
        }
    }

    public interface OnCommunityListener {
        void onMoreOptionCallback(View view, int position);
    }


}
