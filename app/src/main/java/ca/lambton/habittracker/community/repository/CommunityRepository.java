package ca.lambton.habittracker.community.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;

public abstract class CommunityRepository {
    private final static String TAG = CommunityRepository.class.getName();
    protected FirebaseFirestore dbFirebase;
    private MutableLiveData<PostComment> postCommentMutable = new MutableLiveData<>();
    protected FirebaseUser firebaseUser;


    public CommunityRepository() {
        dbFirebase = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dbFirebase.setFirestoreSettings(settings);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<PostComment> fetchPostFromCacheDB(String postId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("community");

        colRef.orderBy("post.creationDate", Query.Direction.DESCENDING)
                .where(Filter.equalTo("post.", ""))

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
                                postCommentMutable.postValue(postComment);
                            }

                        }
                    }

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error getting documents from cache", e));

        return postCommentMutable;
    }
}
