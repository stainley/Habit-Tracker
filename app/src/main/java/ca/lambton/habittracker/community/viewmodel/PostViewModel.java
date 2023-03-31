package ca.lambton.habittracker.community.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;
import ca.lambton.habittracker.community.repository.PostRepository;

public class PostViewModel extends ViewModel {

    private final PostRepository postRepository;

    public PostViewModel(Application application) {
        this.postRepository = new PostRepository(application);
    }

    public void createPost(Post post) {
        this.postRepository.createPost(post);
    }

    public LiveData<List<PostComment>> fetchAllPostWithComments() {
        return this.postRepository.fetchAllPostWithComment();
    }
}
