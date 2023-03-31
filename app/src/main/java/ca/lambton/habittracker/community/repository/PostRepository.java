package ca.lambton.habittracker.community.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.community.dao.CommentDao;
import ca.lambton.habittracker.community.dao.PostDao;
import ca.lambton.habittracker.community.model.Comment;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;

public class PostRepository {
    private static final String TAG = PostRepository.class.getName();
    private final PostDao postDao;
    private final CommentDao commentDao;
    private final FirebaseFirestore dbFirebase;

    public PostRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        commentDao = db.commentDao();
        postDao = db.postDao();
        dbFirebase = FirebaseFirestore.getInstance();
    }

    public void createPost(@NonNull Post post) {
        //AppDatabase.databaseWriterExecutor.execute(() -> postDao.savePost(post));
        createPostCloud(post);
    }

    public void saveAllPost(List<Post> posts) {
        AppDatabase.databaseWriterExecutor.execute(() -> postDao.saveAllPost(posts));
    }


    public void createPostCloud(@NotNull Post post) {

        Map<String, Post> postComment = new HashMap<>();
        postComment.put("post", post);

        dbFirebase.collection("community")
                .add(postComment)
                .addOnSuccessListener(documentReference -> Log.i(TAG, "Document added with ID: " + documentReference.getId()))
                .addOnFailureListener(failure -> Log.e(TAG, "Error adding document"));

    }

    public void fetchData(DataFetchCallback callback) {
        List<Post> postsResult = new ArrayList<>();

        dbFirebase.collection("community")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    querySnapshot.forEach(queryDocumentSnapshot -> {
                        Post post = queryDocumentSnapshot.get("post", Post.class);
                        if (post != null)
                            post.setPostId(queryDocumentSnapshot.getId());
                        //AppDatabase.databaseWriterExecutor.execute(() -> postDao.savePost(post));
                        postsResult.add(post);
                    });

                    // Save data to Room database
                    this.saveAllPost(postsResult);

                    // Notify callback with updated data
                    callback.onDataFetched(postsResult);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    public void deletePost(@NonNull Post post) {
        AppDatabase.databaseWriterExecutor.execute(() -> postDao.deletePost(post));
    }

    public LiveData<List<Post>> fetchAllPost() {
        return postDao.getAllPost();
    }

    public LiveData<List<Post>> fetchAllMyPost(String userId) {
        return postDao.getAllMyPost(userId);
    }

    public LiveData<List<PostComment>> fetchAllPostWithComment() {
        // fetch locally then from the server
        this.fetchData(postData -> postDao.getAllPostWithComments());
        return postDao.getAllPostWithComments();
    }

    public void saveComment(@NonNull Comment comment) {
        AppDatabase.databaseWriterExecutor.execute(() -> commentDao.saveComment(comment));
    }

    public void deleteComment(@NotNull Comment comment) {
        AppDatabase.databaseWriterExecutor.execute(() -> commentDao.deleteComment(comment));
    }

    public interface DataFetchCallback {
        void onDataFetched(List<Post> postData);
    }
}
