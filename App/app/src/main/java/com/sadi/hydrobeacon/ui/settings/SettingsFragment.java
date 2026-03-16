package com.sadi.hydrobeacon.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sadi.hydrobeacon.databinding.FragmentSettingsBinding;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fix: Use requireActivity() to share ViewModel instance with DashboardFragment
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getLowThreshold().observe(getViewLifecycleOwner(), value -> {
            binding.sliderCaution.setValue(value);
            binding.textCautionValue.setText(String.format(Locale.getDefault(), "%d%%", value));
        });

        viewModel.getHighThreshold().observe(getViewLifecycleOwner(), value -> {
            binding.sliderDanger.setValue(value);
            binding.textDangerValue.setText(String.format(Locale.getDefault(), "%d%%", value));
        });

        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled);
        });

        viewModel.getAlertSoundEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchSound.setChecked(enabled);
        });
    }

    private void setupListeners() {
        binding.sliderCaution.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                binding.textCautionValue.setText(String.format(Locale.getDefault(), "%d%%", (int) value));
            }
        });

        binding.sliderCaution.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int newVal = (int) slider.getValue();
                Integer high = viewModel.getHighThreshold().getValue();
                if (high != null && newVal >= high) {
                    showValidationError("Caution level must be lower than Danger level");
                    slider.setValue(viewModel.getLowThreshold().getValue());
                    binding.textCautionValue.setText(String.format(Locale.getDefault(), "%d%%", viewModel.getLowThreshold().getValue()));
                } else {
                    viewModel.setLowThreshold(newVal);
                }
            }
        });

        binding.sliderDanger.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                binding.textDangerValue.setText(String.format(Locale.getDefault(), "%d%%", (int) value));
            }
        });

        binding.sliderDanger.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int newVal = (int) slider.getValue();
                Integer low = viewModel.getLowThreshold().getValue();
                if (low != null && newVal <= low) {
                    showValidationError("Danger level must be higher than Caution level");
                    slider.setValue(viewModel.getHighThreshold().getValue());
                    binding.textDangerValue.setText(String.format(Locale.getDefault(), "%d%%", viewModel.getHighThreshold().getValue()));
                } else {
                    viewModel.setHighThreshold(newVal);
                }
            }
        });

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationsEnabled(isChecked);
        });

        binding.switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setAlertSoundEnabled(isChecked);
        });
    }

    private void showValidationError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
