package com.sadi.hydrobeacon.util;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import java.util.List;

/**
 * A simple predictor for flood forecasting.
 * In a real-world scenario, this would load a TFLite model.
 * For now, it uses simple linear regression to predict the next level.
 */
public class FloodPredictor {

    public double predictNextLevel(List<WaterLevel> history) {
        if (history == null || history.size() < 2) {
            return -1;
        }

        int n = history.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = history.get(i).getLevel();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // Predict for the next step (index n)
        return slope * n + intercept;
    }
}
