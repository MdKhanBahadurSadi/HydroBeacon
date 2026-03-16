package com.sadi.hydrobeacon.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    private final WaterLevelRepository repository;

    public DashboardViewModelFactory(WaterLevelRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
