package com.sadi.hydrobeacon.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryViewModel extends ViewModel {

    private final List<WaterLevel> historicalData = new ArrayList<>();
    private final MutableLiveData<List<WaterLevel>> historyLiveData = new MutableLiveData<>();
    private final MutableLiveData<float[]> statsLiveData = new MutableLiveData<>(new float[]{0f, 0f, 0f});
    private int currentFilterHours = 1;

    public HistoryViewModel(WaterLevelRepository repository) {
        // Observe repository for new readings
        repository.getWaterLevelUpdates().observeForever(waterLevel -> {
            if (waterLevel != null) {
                historicalData.add(waterLevel);
                updateStats();
                applyFilter();
            }
        });
    }

    public LiveData<List<WaterLevel>> getHistory() {
        return historyLiveData;
    }

    public LiveData<float[]> getStatsLiveData() {
        return statsLiveData;
    }

    public void filterByHours(int hours) {
        this.currentFilterHours = hours;
        applyFilter();
    }

    private void applyFilter() {
        if (historicalData.isEmpty()) {
            historyLiveData.setValue(new ArrayList<>());
            return;
        }

        if (currentFilterHours <= 0) { // All data
            historyLiveData.setValue(new ArrayList<>(historicalData));
            return;
        }

        long cutoff = System.currentTimeMillis() - (currentFilterHours * 3600 * 1000L);
        List<WaterLevel> filtered = historicalData.stream()
                .filter(data -> data.getTimestamp() >= cutoff)
                .collect(Collectors.toList());
        historyLiveData.setValue(filtered);
    }

    private void updateStats() {
        if (historicalData.isEmpty()) {
            statsLiveData.postValue(new float[]{0f, 0f, 0f});
            return;
        }

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        float sum = 0;

        for (WaterLevel data : historicalData) {
            float val = (float) data.getLevelPercentage();
            if (val > max) max = val;
            if (val < min) min = val;
            sum += val;
        }

        float avg = sum / historicalData.size();
        statsLiveData.postValue(new float[]{max, min, avg});
    }

    public float[] getStats() {
        return statsLiveData.getValue();
    }

    public void clearHistory() {
        historicalData.clear();
        updateStats();
        applyFilter();
    }
}