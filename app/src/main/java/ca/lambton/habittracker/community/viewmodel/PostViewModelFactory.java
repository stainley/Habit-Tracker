package ca.lambton.habittracker.community.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
public class PostViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public PostViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PostViewModel(application);
    }
}
