package ca.lambton.habittracker.habit.view.fragment.fragment.quote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QuoteViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public QuoteViewModelFactory(Application application) {
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new QuoteViewModel(application);
    }
}
