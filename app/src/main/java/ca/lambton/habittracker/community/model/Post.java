package ca.lambton.habittracker.community.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import ca.lambton.habittracker.habit.model.User;

@Entity(tableName = "POST_TABLE", indices = @Index(name = "POST_IDX", value = {"POST_ID", "NAME"}))
public class Post implements Serializable {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "POST_ID")
    private String postId;
    @ColumnInfo(name = "MESSAGE")
    private String message;
    @ColumnInfo(name = "POST_CREATION_DATE")
    private String creationDate;

    @ColumnInfo(name = "VISIBLE")
    private int visible;
    @Embedded
    private User user;

    @Ignore
    private PostImage postImage;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public PostImage getPostImage() {
        return postImage;
    }

    public void setPostImage(PostImage postImage) {
        this.postImage = postImage;
    }

    @Override
    public String toString() {
        return "Post{" + "postId='" + postId + '\'' + ", message='" + message + '\'' + ", creationDate='" + creationDate + '\'' + ", isVisible=" + visible + ", user=" + user + '}';
    }
}
