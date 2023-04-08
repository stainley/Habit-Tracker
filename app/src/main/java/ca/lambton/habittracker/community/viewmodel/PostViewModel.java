package ca.lambton.habittracker.community.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;
import ca.lambton.habittracker.community.repository.LikesRepository;
import ca.lambton.habittracker.community.repository.PostRepository;

public class PostViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final LikesRepository likesRepository;

    public PostViewModel(Application application) {
        this.postRepository = new PostRepository(application);
        this.likesRepository = new LikesRepository();
    }

    public void createPost(Post post) {
        this.postRepository.createPost(post);
    }

    public LiveData<List<PostComment>> fetchAllPostWithComments() {
        return this.postRepository.fetchAllPostWithComment();
    }

    public void deletePost(@NonNull Post post) {
        this.postRepository.hidePostCloud(post);
    }

    public void likePost(Post post) {
        this.likesRepository.queryLikes(post);
    }

    public LiveData<String> getLikes() {
        return this.likesRepository.getLikes();
    }
}
