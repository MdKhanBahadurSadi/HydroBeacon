package com.sadi.hydrobeacon.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.remote.FirebaseDataSource;

public class WaterLevelRepository {
    private static WaterLevelRepository instance;
    private final FirebaseDataSource firebaseDataSource;
    private final MutableLiveData<WaterLevel> waterLevelLiveData;
    private final MutableLiveData<Exception> errorLiveData;
    private WaterLevel lastKnownLevel;

    private WaterLevelRepository() {
        this.firebaseDataSource = new FirebaseDataSource();
        this.waterLevelLiveData = new MutableLiveData<>();
        this.errorLiveData = new MutableLiveData<>();
        this.lastKnownLevel = null;
    }

    public static synchronized WaterLevelRepository getInstance() {
        if (instance == null) {
            instance = new WaterLevelRepository();
        }
        return instance;
    }

    public LiveData<WaterLevel> getWaterLevelUpdates() {
        return waterLevelLiveData;
    }

    public LiveData<Exception> getErrorUpdates() {
        return errorLiveData;
    }

    public void startMonitoring() {
        firebaseDataSource.startListening(new FirebaseDataSource.WaterLevelCallback() {
            @Override
            public void onDataChange(WaterLevel waterLevel) {
                lastKnownLevel = waterLevel;
                waterLevelLiveData.postValue(waterLevel);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.postValue(e);
            }
        });
    }

    public void stopMonitoring() {
        firebaseDataSource.stopListening();
    }

    public WaterLevel getLastKnownLevel() {
        return lastKnownLevel;
    }
}
