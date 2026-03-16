package com.sadi.hydrobeacon.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;
import com.sadi.hydrobeacon.util.AiAdvisorService;
import com.sadi.hydrobeacon.util.FloodPredictor;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final WaterLevelRepository repository;
    private final AiAdvisorService aiAdvisorService;
    private final FloodPredictor floodPredictor;
    
    private final MutableLiveData<String> alertLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> connectionStatus = new MutableLiveData<>("CONNECTING...");
    private final MutableLiveData<String> aiAdviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> predictedLevelLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    private final List<WaterLevel> history = new ArrayList<>();
    private int lowThreshold = 20;
    private int highThreshold = 80;
    private int lastNotifiedLevel = -1;

    public DashboardViewModel(WaterLevelRepository repository) {
        this.repository = repository;
        this.aiAdvisorService = new AiAdvisorService();
        this.floodPredictor = new FloodPredictor();
    }

    public LiveData<WaterLevel> getWaterLevel() {
        return repository.getWaterLevelUpdates();
    }

    public LiveData<String> getAiAdvice() {
        return aiAdviceLiveData;
    }

    public LiveData<Double> getPredictedLevel() {
        return predictedLevelLiveData;
    }

    public LiveData<String> getAlerts() {
        return alertLiveData;
    }

    public LiveData<String> getConnectionStatus() {
        return connectionStatus;
    }

    public LiveData<String> getErrors() {
        return errorLiveData;
    }

    public void setThresholds(int low, int high) {
        this.lowThreshold = low;
        this.highThreshold = high;
    }

    public void updateConnectionStatus(String status) {
        connectionStatus.postValue(status);
    }

    public void startMonitoring() {
        connectionStatus.postValue("CONNECTING...");
        repository.startMonitoring();
    }

    public void stopMonitoring() {
        repository.stopMonitoring();
    }

    public void checkAlerts(WaterLevel waterLevel) {
        int level = waterLevel.getLevelPercentage();
        history.add(waterLevel);
        if (history.size() > 20) history.remove(0);

        // Update Prediction
        double prediction = floodPredictor.predictNextLevel(history);
        predictedLevelLiveData.postValue(prediction);

        // Trigger AI Advice if level is high
        if (level >= highThreshold && (lastNotifiedLevel < highThreshold || lastNotifiedLevel == -1)) {
            alertLiveData.setValue("HIGH:" + level);
            fetchAiAdvice(waterLevel.getLevel(), highThreshold);
        } else if (level <= lowThreshold && (lastNotifiedLevel > lowThreshold || lastNotifiedLevel == -1)) {
            alertLiveData.setValue("LOW:" + level);
        }
        
        lastNotifiedLevel = level;
    }

    private void fetchAiAdvice(double currentLevel, double dangerLevel) {
        aiAdvisorService.getAdvice(currentLevel, dangerLevel, new AiAdvisorService.AiResponseCallback() {
            @Override
            public void onResponse(String advice) {
                aiAdviceLiveData.postValue(advice);
            }

            @Override
            public void onError(Throwable t) {
                aiAdviceLiveData.postValue("AI পরামর্শ পেতে সমস্যা হচ্ছে। অনুগ্রহ করে সাবধানে থাকুন।");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.stopMonitoring();
    }
}
