package ca.lambton.habittracker.habit.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import ca.lambton.habittracker.model.Quote;

@Dao
public interface QuoteDao {
    @Insert
    void insertQuote(Quote... quotes);

    @Query("SELECT * FROM QUOTE_TBL WHERE ID = :id")
    LiveData<Quote> getQuoteById(long id);


}
