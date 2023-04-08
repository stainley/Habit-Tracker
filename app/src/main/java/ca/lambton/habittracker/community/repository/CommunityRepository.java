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
    protected FirebaseUser firebaseUser;


    public CommunityRepository() {
        dbFirebase = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dbFirebase.setFirestoreSettings(settings);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

}
