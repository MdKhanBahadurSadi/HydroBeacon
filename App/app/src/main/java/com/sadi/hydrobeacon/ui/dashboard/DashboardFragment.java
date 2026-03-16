package com.sadi.hydrobeacon.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sadi.hydrobeacon.R;
import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;
import com.sadi.hydrobeacon.databinding.FragmentDashboardBinding;
import com.sadi.hydrobeacon.ui.settings.SettingsViewModel;
import com.sadi.hydrobeacon.util.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private SettingsViewModel settingsViewModel;
    private NotificationHelper notificationHelper;
    private String lastSyncTime = "--:--:--";
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationHelper = new NotificationHelper(requireContext());

        WaterLevelRepository repository = WaterLevelRepository.getInstance();
        viewModel = new ViewModelProvider(this, new DashboardViewModelFactory(repository))
                .get(DashboardViewModel.class);

        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        observeSettings();

        setupObservers();
        viewModel.startMonitoring();
    }

    private void observeSettings() {
        settingsViewModel.getLowThreshold().observe(getViewLifecycleOwner(), low -> {
            Integer high = settingsViewModel.getHighThreshold().getValue();
            if (high != null) {
                viewModel.setThresholds(low, high);
            }
        });

        settingsViewModel.getHighThreshold().observe(getViewLifecycleOwner(), high -> {
            Integer low = settingsViewModel.getLowThreshold().getValue();
            if (low != null) {
                viewModel.setThresholds(low, high);
            }
        });
    }

    private void setupObservers() {
        viewModel.getWaterLevel().observe(getViewLifecycleOwner(), waterLevel -> {
            viewModel.updateConnectionStatus("CONNECTED");
            int percentage = waterLevel.getLevelPercentage();
            
            binding.dashboardProgress.setProgress(percentage);
            binding.textPercentage.setText(String.format(Locale.getDefault(), "%d%%", percentage));

            lastSyncTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(waterLevel.getTimestamp()));
            updateSyncDisplay(viewModel.getConnectionStatus().getValue());

            updateStatusColor(percentage);
            viewModel.checkAlerts(waterLevel);
        });

        viewModel.getErrors().observe(getViewLifecycleOwner(), error -> {
            viewModel.updateConnectionStatus("ERROR");
        });

        viewModel.getConnectionStatus().observe(getViewLifecycleOwner(), this::updateSyncDisplay);

        viewModel.getAlerts().observe(getViewLifecycleOwner(), alertInfo -> {
            if (alertInfo == null || !settingsViewModel.getNotificationsEnabled().getValue()) return;

            String[] parts = alertInfo.split(":");
            if (parts.length != 2) return;

            String type = parts[0];
            int level = Integer.parseInt(parts[1]);

            if ("HIGH".equals(type)) {
                String thresholdInfo = "(Danger threshold: " + settingsViewModel.getHighThreshold().getValue() + "%)";
                notificationHelper.sendFloodAlert(level, thresholdInfo);
            } else if ("LOW".equals(type)) {
                String thresholdInfo = "caution threshold (" + settingsViewModel.getLowThreshold().getValue() + "%)";
                notificationHelper.sendLowLevelAlert(level, thresholdInfo);
            }
        });

        // New AI Observers
        viewModel.getPredictedLevel().observe(getViewLifecycleOwner(), prediction -> {
            if (prediction != null && prediction >= 0) {
                binding.textPredictionValue.setText(String.format(Locale.getDefault(), "%.1f%%", prediction));
            }
        });

        viewModel.getAiAdvice().observe(getViewLifecycleOwner(), advice -> {
            if (advice != null) {
                binding.textAiAdvice.setText(advice);
            }
        });
    }

    private void updateSyncDisplay(String status) {
        if (status == null) return;
        
        String emoji;
        switch (status) {
            case "CONNECTED":
                emoji = "🟢";
                break;
            case "ERROR":
                emoji = "🔴";
                break;
            case "CONNECTING...":
            default:
                emoji = "🟡";
                break;
        }
        
        String displayText = String.format(Locale.getDefault(), "%s Synced: %s", emoji, lastSyncTime);
        binding.textSyncTime.setText(displayText);
    }

    private void updateStatusColor(int percentage) {
        int color;
        String status;

        Integer highThreshold = settingsViewModel.getHighThreshold().getValue();
        Integer lowThreshold = settingsViewModel.getLowThreshold().getValue();
        
        if (highThreshold == null) highThreshold = 80;
        if (lowThreshold == null) lowThreshold = 60;

        if (percentage >= highThreshold) {
            color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light);
            status = "CRITICAL";
            binding.alertBanner.setVisibility(View.VISIBLE);
        } else if (percentage >= lowThreshold) {
            color = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light);
            status = "WARNING";
            binding.alertBanner.setVisibility(View.GONE);
        } else {
            color = ContextCompat.getColor(requireContext(), android.R.color.holo_green_light);
            status = "SAFE";
            binding.alertBanner.setVisibility(View.GONE);
        }

        binding.dashboardProgress.setIndicatorColor(color);
        binding.textStatus.setText(status);
        binding.textStatus.setTextColor(color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
