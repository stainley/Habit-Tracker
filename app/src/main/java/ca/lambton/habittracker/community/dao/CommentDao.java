package ca.lambton.habittracker.community.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import java.util.List;

import ca.lambton.habittracker.community.model.Comment;

@Dao
public abstract class CommentDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    public abstract void saveComment(Comment comment);
    @Delete
    public abstract void deleteComment(Comment comment);

}
