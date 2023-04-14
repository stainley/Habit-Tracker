package ca.lambton.habittracker.community.view.adapter;

import static android.view.ViewGroup.LayoutParams.*;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
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
        Post post = posts.get(position);
        User user = post.getUser();

        holder.authorText.setText(user.getName());
        holder.postText.setText(post.getMessage());
        holder.likesText.setText(String.valueOf(post.getCount()));
        holder.dayPostedTxt.setText(getFormattedDateTime(post.getCreationDate()));

        setProfilePhoto(holder.profilePhoto, user.getPhotoUrl());
        setPostImage(holder.postImage, post.getPostImage().getPath());


        communityListener.onMoreOptionCallback(holder.moreOptionButton, position);

        setMoreOptionClickListener(holder.postCardView, position);

        setDoubleTapListener(holder.postCardView, holder.likesText, holder.getAdapterPosition());
    }

    private String getFormattedDateTime(String creationDate) {
        LocalDateTime dateTime = LocalDateTime.parse(creationDate, DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    private void setProfilePhoto(ImageView imageView, String photoUrl) {
        if (TextUtils.isEmpty(photoUrl)) {
            imageView.setVisibility(View.GONE);
            return;
        }

        Picasso.get().load(photoUrl).fit().into(imageView);
    }

    private void setPostImage(ImageView imageView, String path) {
        if (TextUtils.isEmpty(path)) {
            imageView.setVisibility(View.GONE);
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        imageView.setClipToOutline(true);

        Picasso.get().load(path)
                .transform(new CropTransformation(0.7f, 0.8f, CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                .transform(new RoundedCornersTransformation(24, 24, RoundedCornersTransformation.CornerType.ALL))
                .error(R.drawable.placeholder_image)
                .into(imageView);

    }

    private void setMoreOptionClickListener(@NonNull View view, int position) {
        view.setOnClickListener(v -> communityListener.onMoreOptionCallback(view, position));
    }

    private void setDoubleTapListener(@NonNull View view, TextView textView, int position) {
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                communityListener.onMoreOptionCallback(textView, position);
                return true;
            }
        });

        view.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
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
        private final TextView likesText;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);

            authorText = itemView.findViewById(R.id.author_text);
            postText = itemView.findViewById(R.id.post_text);
            dayPostedTxt = itemView.findViewById(R.id.day_posted);
            profilePhoto = itemView.findViewById(R.id.profile_picture);
            moreOptionButton = itemView.findViewById(R.id.more_options_button);

            postImage = itemView.findViewById(R.id.post_picture);
            postCardView = itemView.findViewById(R.id.post_card);
            likesText = itemView.findViewById(R.id.tvLike);

        }


    }

    public interface OnCommunityListener {
        void onMoreOptionCallback(View view, int position);
    }


}
