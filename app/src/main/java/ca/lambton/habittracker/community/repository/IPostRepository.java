package ca.lambton.habittracker.community.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;

public interface IPostRepository {
    void createPostCloud(@NotNull Post post);

    void fetchData(PostRepository.DataFetchCallback callback);

    void deletePost(@NonNull Post post);

    void hidePostCloud(@NonNull Post post);

    LiveData<List<PostComment>> fetchAllFromCacheDB();

    LiveData<List<PostComment>> fetchAllPostWithComment();

}
