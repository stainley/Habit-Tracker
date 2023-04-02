package ca.lambton.habittracker.community.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

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
    MutableLiveData<List<PostComment>> postCommentMutable = new MutableLiveData<>();
    private final PostDao postDao;
    private final CommentDao commentDao;
    private final FirebaseFirestore dbFirebase;

    public PostRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        commentDao = db.commentDao();
        postDao = db.postDao();
        dbFirebase = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dbFirebase.setFirestoreSettings(settings);
    }

    public void createPost(@NonNull Post post) {
        createPostCloud(post);
    }

    public void saveAllPost(List<Post> posts) {
        //AppDatabase.databaseWriterExecutor.execute(() -> postDao.saveAllPost(posts));
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
        List<PostComment> postsResult = new ArrayList<>();

        Query query = dbFirebase.collection("community").where(Filter.equalTo("post.visible", 1));
        query.orderBy("post.creationDate", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(querySnapshot -> {
            querySnapshot.forEach(queryDocumentSnapshot -> {
                Post post = queryDocumentSnapshot.get("post", Post.class);
                if (post != null) post.setPostId(queryDocumentSnapshot.getId());

                PostComment postComment = new PostComment();
                postComment.post = post;

                postsResult.add(postComment);
            });

            callback.onDataFetched(postsResult);
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }

    public void deletePost(@NonNull Post post) {
        AppDatabase.databaseWriterExecutor.execute(() -> postDao.deletePost(post));
    }


    public void hidePostCloud(Post post) {
        post.setVisible(0);

        DocumentReference docRef = dbFirebase.collection("community").document(post.getPostId());
        // Update all other devices to delete the document locally
        Map<String, Post> updates = new HashMap<>();
        updates.put("post", post);

        docRef.set(updates, SetOptions.merge()).addOnSuccessListener(unused -> {
            Log.i(TAG, "Post " + post.getPostId() + " successfully deleted!");
            docRef.delete();
            this.fetchData(postData -> fetchAllFromCacheDB());
        }).addOnFailureListener(e -> Log.w(TAG, "Error deleting document: " + post.getPostId(), e));

    }

    /**
     * Hide the
     *
     * @param post
     */
    public void deletePostCloud(@NotNull Post post) {
        DocumentReference docRef = dbFirebase.collection("community").document(post.getPostId());

        // Update all other devices to delete the document locally
        Map<String, Object> updates = new HashMap<>();
        updates.put("deleted", true);
        docRef.update(updates);

        docRef.delete().addOnSuccessListener(unused -> {
            Log.i(TAG, "Post " + post.getPostId() + " successfully deleted!");
            docRef.delete();
            deletePost(post);


        }).addOnFailureListener(e -> Log.w(TAG, "Error deleting document: " + post.getPostId(), e));

    }

    public LiveData<List<Post>> fetchAllPost() {
        return postDao.getAllPost();
    }

    public LiveData<List<PostComment>> fetchAllFromCacheDB() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("community");

        colRef.orderBy("post.creationDate", Query.Direction.DESCENDING)
                .get(Source.CACHE)
                .addOnSuccessListener(querySnapshot -> {
                    List<PostComment> posts = new ArrayList<>();


                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document data is available in the local cache
                            Post post = documentSnapshot.get("post", Post.class);
                            if (post != null) post.setPostId(documentSnapshot.getId());

                            PostComment postComment = new PostComment();
                            if (post != null && post.getVisible() == 1) {
                                postComment.post = post;
                                posts.add(postComment);
                            }

                        }
                    }
                    postCommentMutable.postValue(posts);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error getting documents from cache", e));

        return postCommentMutable;
    }

    public LiveData<List<Post>> fetchAllMyPost(String userId) {
        return postDao.getAllMyPost(userId);
    }

    public LiveData<List<PostComment>> fetchAllPostWithComment() {
        // fetch locally then from the server
        this.fetchData(postCommentMutable::postValue);
        return postCommentMutable;
    }

    public void saveComment(@NonNull Comment comment) {
        AppDatabase.databaseWriterExecutor.execute(() -> commentDao.saveComment(comment));
    }

    public void deleteComment(@NotNull Comment comment) {
        AppDatabase.databaseWriterExecutor.execute(() -> commentDao.deleteComment(comment));
    }

    public interface DataFetchCallback {
        void onDataFetched(List<PostComment> postData);
    }
}
