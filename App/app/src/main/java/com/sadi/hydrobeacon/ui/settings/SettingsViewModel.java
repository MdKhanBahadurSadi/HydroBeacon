package com.sadi.hydrobeacon.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends AndroidViewModel {

    private static final String PREFS_NAME = "hydrobeacon_settings";
    private static final String KEY_LOW_THRESHOLD = "low_threshold";
    private static final String KEY_HIGH_THRESHOLD = "high_threshold";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_ALERT_SOUND_ENABLED = "alert_sound_enabled";

    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Integer> lowThreshold = new MutableLiveData<>();
    private final MutableLiveData<Integer> highThreshold = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> alertSoundEnabled = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
    }

    private void loadSettings() {
        lowThreshold.setValue(sharedPreferences.getInt(KEY_LOW_THRESHOLD, 60));
        highThreshold.setValue(sharedPreferences.getInt(KEY_HIGH_THRESHOLD, 80));
        notificationsEnabled.setValue(sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true));
        alertSoundEnabled.setValue(sharedPreferences.getBoolean(KEY_ALERT_SOUND_ENABLED, true));
    }

    public LiveData<Integer> getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(int value) {
        lowThreshold.setValue(value);
        sharedPreferences.edit().putInt(KEY_LOW_THRESHOLD, value).apply();
    }

    public LiveData<Integer> getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(int value) {
        highThreshold.setValue(value);
        sharedPreferences.edit().putInt(KEY_HIGH_THRESHOLD, value).apply();
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean enabled) {
        notificationsEnabled.setValue(enabled);
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public LiveData<Boolean> getAlertSoundEnabled() {
        return alertSoundEnabled;
    }

    public void setAlertSoundEnabled(boolean enabled) {
        alertSoundEnabled.setValue(enabled);
        sharedPreferences.edit().putBoolean(KEY_ALERT_SOUND_ENABLED, enabled).apply();
    }
}
