package ca.lambton.habittracker.community.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PostComment {
    @Embedded
    public Post post;
    @Relation(entity = Comment.class, parentColumn = "POST_ID", entityColumn = "PARENT_POST_ID")
    public List<Comment> comments;

}
