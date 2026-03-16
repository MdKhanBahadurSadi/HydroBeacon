package com.sadi.hydrobeacon.ui.history;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sadi.hydrobeacon.R;
import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;
import com.sadi.hydrobeacon.databinding.FragmentHistoryBinding;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        WaterLevelRepository repository = WaterLevelRepository.getInstance();
        HistoryViewModelFactory factory = new HistoryViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(HistoryViewModel.class);

        setupChart();
        setupObservers();
        setupFilters();
    }

    private void setupChart() {
        binding.historyChart.getDescription().setEnabled(false);
        binding.historyChart.getLegend().setEnabled(false);
        binding.historyChart.setTouchEnabled(true);
        binding.historyChart.setDragEnabled(true);
        binding.historyChart.setScaleEnabled(true);
        binding.historyChart.setPinchZoom(true);
        binding.historyChart.setDrawGridBackground(false);
        binding.historyChart.setBackgroundColor(Color.TRANSPARENT);
        binding.historyChart.setDrawBorders(false);

        int textColor = Color.parseColor("#8899AA");
        int gridColor = Color.parseColor("#2A3A4A");

        XAxis xAxis = binding.historyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setLabelCount(6);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        YAxis leftAxis = binding.historyChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(textColor);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(gridColor);
        leftAxis.setGridLineWidth(1f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        binding.historyChart.getAxisRight().setEnabled(false);

        // Danger Threshold Line (80%)
        LimitLine dangerLine = new LimitLine(80f, "Danger 80%");
        dangerLine.setLineColor(Color.parseColor("#FF4757"));
        dangerLine.setLineWidth(2f);
        dangerLine.setTextColor(Color.parseColor("#FF4757"));
        dangerLine.setTextSize(10f);
        leftAxis.addLimitLine(dangerLine);

        // Caution Threshold Line (60%)
        LimitLine cautionLine = new LimitLine(60f, "Caution");
        cautionLine.setLineColor(Color.parseColor("#FFA502"));
        cautionLine.setLineWidth(1f);
        cautionLine.enableDashedLine(10f, 10f, 0f);
        cautionLine.setTextColor(Color.parseColor("#FFA502"));
        cautionLine.setTextSize(10f);
        leftAxis.addLimitLine(cautionLine);
    }

    private void setupObservers() {
        viewModel.getHistory().observe(getViewLifecycleOwner(), history -> {
            updateChartData(history);
            updateStats(history);
        });
    }

    private void updateChartData(List<WaterLevel> history) {
        if (history == null || history.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();
        for (WaterLevel data : history) {
            entries.add(new Entry(data.getTimestamp(), (float) data.getLevelPercentage()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Water Level");
        int primaryColor = Color.parseColor("#00C2FF");
        
        dataSet.setColor(primaryColor);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(primaryColor);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);

        // Gradient Fill
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(100, 0, 194, 255), Color.TRANSPARENT}
        );
        dataSet.setFillDrawable(gradient);

        LineData lineData = new LineData(dataSet);
        binding.historyChart.setData(lineData);
        binding.historyChart.invalidate();
    }

    private void updateStats(List<WaterLevel> history) {
        if (history == null || history.isEmpty()) {
            binding.textMaxLevel.setText("--");
            binding.textMinLevel.setText("--");
            binding.textAvgLevel.setText("--");
            return;
        }

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (WaterLevel level : history) {
            double val = level.getLevelPercentage();
            if (val > max) max = val;
            if (val < min) min = val;
            sum += val;
        }

        double avg = sum / history.size();

        binding.textMaxLevel.setText(String.format(Locale.getDefault(), "%.1f%%", max));
        binding.textMinLevel.setText(String.format(Locale.getDefault(), "%.1f%%", min));
        binding.textAvgLevel.setText(String.format(Locale.getDefault(), "%.1f%%", avg));
    }

    private void setupFilters() {
        binding.timeFilterGroup.check(R.id.chip1h);
        viewModel.filterByHours(1);

        binding.timeFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip1h) {
                viewModel.filterByHours(1);
            } else if (checkedId == R.id.chip6h) {
                viewModel.filterByHours(6);
            } else if (checkedId == R.id.chip24h) {
                viewModel.filterByHours(24);
            } else if (checkedId == R.id.chipAll) {
                viewModel.filterByHours(-1); // Assuming -1 means all
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}