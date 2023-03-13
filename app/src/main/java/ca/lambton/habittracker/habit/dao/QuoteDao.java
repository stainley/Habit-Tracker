package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import ca.lambton.habittracker.model.Quote;

@Dao
public abstract class QuoteDao {

    @Query("SELECT * FROM Quote")
    public abstract LiveData<Quote> getQuoteById(long id);
}
