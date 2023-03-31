package ca.lambton.habittracker.community.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;

@Dao
public abstract class PostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void savePost(Post post);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void saveAllPost(List<Post> posts);

    @Delete
    public abstract void deletePost(Post post);

    @Query(value = "SELECT * FROM POST_TABLE ORDER BY POST_CREATION_DATE ASC")
    public abstract LiveData<List<Post>> getAllPost();

    @Query(value = "SELECT * FROM POST_TABLE WHERE USER_ID = :userId")
    public abstract LiveData<List<Post>> getAllMyPost(String userId);

    @Transaction
    @Query(value = "SELECT * FROM POST_TABLE")
    public abstract LiveData<List<PostComment>> getAllPostWithComments();

}
