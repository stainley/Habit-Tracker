package ca.lambton.habittracker.community.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ca.lambton.habittracker.habit.model.User;

@Entity(tableName = "COMMENT_TBL",
        foreignKeys = @ForeignKey(entity = Post.class, parentColumns = "POST_ID", childColumns = "PARENT_POST_ID", onDelete = ForeignKey.CASCADE),
        indices = @Index(value = "PARENT_POST_ID", name = "COMMENT_IDX")
)
public class Comment implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "COMMENT_ID")
    private long id;
    @ColumnInfo(name = "MESSAGE")
    private String message;
    @ColumnInfo(name = "CREATION_DATE")
    private String creationDate;
    @ColumnInfo(name = "PARENT_POST_ID")
    private long postId;
    @Embedded
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
