package ca.lambton.habittracker.community.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import ca.lambton.habittracker.community.model.Like;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.model.PostComment;

public class LikesRepository extends CommunityRepository {

    private final MutableLiveData<String> likeMutableLiveData = new MutableLiveData<>();

    // Query the Firestore database for a post's likes
    public void queryLikes(@NotNull Post post) {

        Query query = dbFirebase.collection("likes")
                .whereEqualTo("postId", post.getPostId())
                .whereEqualTo("userId", firebaseUser.getUid());

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                // User has already liked the post
                queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                    String postId = queryDocumentSnapshot.get("postId", String.class);
                    String userId = queryDocumentSnapshot.get("userId", String.class);

                    if (post.getPostId().equals(postId) && userId.equals(firebaseUser.getUid())) {
                        unlikePost(post, queryDocumentSnapshot);
                    }
                });
            } else {
                // User has not yet liked the post
                likePost(post);
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            System.out.println();
        });
    }

    // Like a post by incrementing the likes_count field and adding a new like document
    private void likePost(@NotNull Post post) {
        DocumentReference postRef = dbFirebase.collection("community").document(post.getPostId());

        dbFirebase.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(postRef);
            int likesCount = snapshot.getLong("post.count").intValue() + 1;
            transaction.update(snapshot.getReference(), "post.count", likesCount);
            transaction.update(postRef, "likes_count", likesCount);
            transaction.set(dbFirebase.collection("likes").document(), new Like(post.getPostId(), firebaseUser.getUid()));
            return likesCount;
        }).addOnSuccessListener(result -> {
            // Update the UI to reflect the user's like status
            Long count = Long.valueOf(result);
            likeMutableLiveData.postValue(String.valueOf(count));
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }

    // Unlike a post by decrementing the likes_count field and deleting the corresponding like document
    private void unlikePost(@NotNull Post post, DocumentSnapshot documentSnapshot) {

        DocumentReference postRef = dbFirebase.collection("community").document(post.getPostId());

        dbFirebase.runTransaction(transaction -> {

            DocumentSnapshot snapshot = transaction.get(postRef);
            int likesCount = snapshot.getLong("post.count").intValue() - 1;
            transaction.update(snapshot.getReference(), "post.count", likesCount);
            transaction.update(postRef, "likes_count", likesCount);
            transaction.delete(documentSnapshot.getReference());
            return likesCount;
        }).addOnSuccessListener(result -> {
            // Update the UI to reflect the user's like status
            Long count = Long.valueOf(result);
            likeMutableLiveData.setValue(String.valueOf(count));

        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }

    public MutableLiveData<String> getLikes() {
        return likeMutableLiveData;
    }
}
