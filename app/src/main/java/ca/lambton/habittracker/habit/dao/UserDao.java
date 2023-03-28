package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ca.lambton.habittracker.habit.model.User;

@Dao
public abstract class UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUser(User user);

    @Query("SELECT * FROM USER_TBL WHERE EMAIL = :email")
    public abstract LiveData<User> getUserByEmail(String email);
}
