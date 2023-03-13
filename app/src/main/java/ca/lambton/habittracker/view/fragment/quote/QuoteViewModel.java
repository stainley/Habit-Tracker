package ca.lambton.habittracker.view.fragment.quote;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import ca.lambton.habittracker.habit.repository.QuoteRepository;
import ca.lambton.habittracker.model.Quote;

public class QuoteViewModel extends ViewModel {

    private final QuoteRepository repository;

    public QuoteViewModel(Application application) {
        this.repository = new QuoteRepository(application);
    }

    LiveData<Quote> getQuote(long id) {
        return this.repository.getQuoteById(id);
    }
}
