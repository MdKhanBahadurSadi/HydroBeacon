package com.sadi.hydrobeacon.data.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class WaterLevelTest {

    @Test
    public void testGetLevelPercentage_returnsFloorOfLevel() {
        WaterLevel waterLevel = new WaterLevel(72.9, 1234567890L);
        assertEquals(72, waterLevel.getLevelPercentage());
    }

    @Test
    public void testGetLevelPercentage_exactValue() {
        WaterLevel waterLevel = new WaterLevel(80.0, 1234567890L);
        assertEquals(80, waterLevel.getLevelPercentage());
    }

    @Test
    public void testGetTimestamp_returnsCorrectValue() {
        long timestamp = 1234567890L;
        WaterLevel waterLevel = new WaterLevel(50.0, timestamp);
        assertEquals(timestamp, waterLevel.getTimestamp());
    }

    @Test
    public void testDefaultConstructor() {
        WaterLevel waterLevel = new WaterLevel();
        assertEquals(0.0, waterLevel.getLevel(), 0.001);
        assertEquals(0L, waterLevel.getTimestamp());
    }

    @Test
    public void testSetters() {
        WaterLevel waterLevel = new WaterLevel();
        
        waterLevel.setLevel(45.5);
        assertEquals(45.5, waterLevel.getLevel(), 0.001);
        
        long newTimestamp = 9876543210L;
        waterLevel.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, waterLevel.getTimestamp());
    }
}