package com.sadi.hydrobeacon.ui.history;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;

public class HistoryViewModelFactory implements ViewModelProvider.Factory {

    private final WaterLevelRepository repository;

    public HistoryViewModelFactory(WaterLevelRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) new HistoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
