package ca.lambton.habittracker.habit.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.dao.ProgressDao;
import ca.lambton.habittracker.habit.dao.UserDao;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.model.User;

public class HabitRepository {

    private static final String TAG = HabitRepository.class.getName();

    private final HabitDao habitDao;
    private final ProgressDao progressDao;
    private final UserDao userDao;
    private final FirebaseFirestore dbFirebase;

    public HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        habitDao = db.habitDao();
        progressDao = db.progressDao();
        userDao = db.userDao();

        dbFirebase = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Habit>> getAllHabitByCategory(long category) {
        return habitDao.getAllHabitsByCategory(category);
    }

    public LiveData<List<Habit>> getAllHabit() {
        return habitDao.getAllHabits();
    }

    public LiveData<Habit> getHabitById(long id) {
        return habitDao.fetchById(id);
    }

    public LiveData<Habit> getHabitByName(String name) {
        return habitDao.fetchByName(name);
    }

    public void save(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.insertHabit(habit));
    }

    public void update(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.update(habit));
    }

    public void delete(Habit habit) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.delete(habit));
    }

    public LiveData<List<HabitProgress>> getHabitProgress(long habitId) {
        return progressDao.getProgressByHabit(habitId);
    }


    public LiveData<List<HabitProgress>> getHabitProgress() {
        return progressDao.getAllProgress();
    }

    public List<HabitProgress> getHabitProgressNotLive() {
        List<HabitProgress> allProgressNotLive = new ArrayList<>();

        AppDatabase.databaseWriterExecutor.execute(() -> allProgressNotLive.addAll(progressDao.getAllProgressNotLive()));

        return allProgressNotLive;
    }

    public LiveData<List<Habit>> fetchAllMyHabit(long userId) {
        return habitDao.fetchAllMyHabits(userId);
    }

    public void increaseHabit(Progress progress) {
        AppDatabase.databaseWriterExecutor.execute(() -> progressDao.insertProgress(progress));
    }

    public void decreaseHabit(long progressId) {
        AppDatabase.databaseWriterExecutor.execute(() -> progressDao.decreaseProgress(progressId));
    }

    public void insertHabitProgress(Habit habit, List<Progress> progressList) {
        AppDatabase.databaseWriterExecutor.execute(() -> habitDao.insertProgressHabit(habit, progressList));
    }

    public LiveData<List<Progress>> getTodayAllMyHabitProgress() {

        return progressDao.getTodayAllMyHabitProgress();
    }

    public void saveUser(User user) {
        AppDatabase.databaseWriterExecutor.execute(() -> userDao.saveUser(user));
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void saveCloudHabit(Habit habit) {

        // Create a reference to the collection
        CollectionReference collectionRef = FirebaseFirestore.getInstance()
                .collection("habit").document(habit.getUserId()).collection("public");

        //db.collection('users').where('interests', 'array-contains', {name: "soccer", kind:"sports"})
        // Create a query to check if the document already exists
        Query query = collectionRef
                .where(Filter.and
                        (Filter.equalTo("habit", habit),
                                Filter.equalTo("habit.id", habit.getId()),
                                Filter.equalTo("habit.userId", habit.getUserId())));


        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    // Document already exists
                    querySnapshot.getDocuments().forEach(documentSnapshot -> System.out.println(documentSnapshot.get("habit")));
                } else {

                    System.out.println("Inner result: " + habit);

                    // Document does not exist, so insert it
                    Map<String, Habit> habitMap = new HashMap<>();
                    habitMap.put("habit", habit);

                    dbFirebase.collection("habit")
                            .document(habit.getUserId())
                            .collection("public")
                            .add(habitMap)
                            .addOnSuccessListener(documentReference -> Log.i(TAG, "Document added with ID: " + documentReference.getId()))
                            .addOnFailureListener(failure -> Log.e(TAG, "Error adding document"));

                    //collectionRef.add(habitMap);
                }
            } else {
                // Handle errors
                Log.e(TAG, "An error had occurred");
            }
        });

    }

    public LiveData<List<Habit>> getAllHabitCloud() {

        MutableLiveData<List<Habit>> habitListMutableData = new MutableLiveData<>();
        Query query = FirebaseFirestore.getInstance().collectionGroup("public");

        // Execute the query
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Habit> habits = new ArrayList<>();

            queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                String documentId = queryDocumentSnapshot.getId();
                Habit habit = queryDocumentSnapshot.get("habit", Habit.class);

                System.out.println(queryDocumentSnapshot.get("habit.id"));
                habits.add(habit);


                System.out.println(documentId);

                habitListMutableData.setValue(habits);
            });
        });

        return habitListMutableData;
    }
}
