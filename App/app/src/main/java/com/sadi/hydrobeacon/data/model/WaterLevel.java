package com.sadi.hydrobeacon.data.model;

public class WaterLevel {
    private double level;
    private long timestamp;

    public WaterLevel() {
        // Default constructor required for calls to DataSnapshot.getValue(WaterLevel.class)
    }

    public WaterLevel(double level, long timestamp) {
        this.level = level;
        this.timestamp = timestamp;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLevelPercentage() {
        return (int) Math.floor(level);
    }
}
