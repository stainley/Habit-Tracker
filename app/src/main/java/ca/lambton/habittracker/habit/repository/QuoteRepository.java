package ca.lambton.habittracker.habit.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.habit.dao.QuoteDao;
import ca.lambton.habittracker.model.Quote;

public class QuoteRepository {
    private final QuoteDao quoteDao;

    public QuoteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        quoteDao = db.quoteDao();
    }

    public LiveData<Quote> getQuoteById(long id) {
        return quoteDao.getQuoteById(id);
    }
}
