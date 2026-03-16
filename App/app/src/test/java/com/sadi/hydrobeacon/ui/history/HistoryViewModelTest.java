package com.sadi.hydrobeacon.ui.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.sadi.hydrobeacon.data.repository.WaterLevelRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HistoryViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HistoryViewModel viewModel;
    private FakeWaterLevelRepository fakeRepository;

    @Before
    public void setup() {
        fakeRepository = new FakeWaterLevelRepository();
        viewModel = new HistoryViewModel(fakeRepository);
    }

    @Test
    public void testAddData_updatesHistory() throws InterruptedException {
        fakeRepository.pushData(new WaterLevel(10.0, System.currentTimeMillis()));
        fakeRepository.pushData(new WaterLevel(20.0, System.currentTimeMillis()));
        fakeRepository.pushData(new WaterLevel(30.0, System.currentTimeMillis()));

        List<WaterLevel> history = getOrAwaitValue(viewModel.getHistory());
        assertNotNull(history);
        assertEquals(3, history.size());
    }

    @Test
    public void testFilterByHours_filtersCorrectly() throws InterruptedException {
        long now = System.currentTimeMillis();
        // Entry within 1 hour (30 minutes ago)
        fakeRepository.pushData(new WaterLevel(10.0, now - 30 * 60 * 1000L));
        // Entry outside 1 hour (2 hours ago)
        fakeRepository.pushData(new WaterLevel(20.0, now - 2 * 3600 * 1000L));

        viewModel.filterByHours(1);

        List<WaterLevel> history = getOrAwaitValue(viewModel.getHistory());
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(10.0, history.get(0).getLevel(), 0.1);
    }

    @Test
    public void testClearHistory_emptiesList() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            fakeRepository.pushData(new WaterLevel(i * 10.0, System.currentTimeMillis()));
        }
        
        assertEquals(5, getOrAwaitValue(viewModel.getHistory()).size());
        
        viewModel.clearHistory();
        
        List<WaterLevel> history = getOrAwaitValue(viewModel.getHistory());
        assertTrue(history.isEmpty());
    }

    @Test
    public void testGetStats_calculatesCorrectly() throws InterruptedException {
        // Stats calculation uses LevelPercentage (floor)
        fakeRepository.pushData(new WaterLevel(60.0, System.currentTimeMillis()));
        fakeRepository.pushData(new WaterLevel(80.0, System.currentTimeMillis()));
        fakeRepository.pushData(new WaterLevel(40.0, System.currentTimeMillis()));

        // We use getOrAwaitValue to ensure the posted stats value is processed
        float[] stats = getOrAwaitValue(viewModel.getStatsLiveData());
        
        assertNotNull(stats);
        assertEquals(80f, stats[0], 0.1f); // MAX
        assertEquals(40f, stats[1], 0.1f); // MIN
        assertEquals(60f, stats[2], 0.1f); // AVG
    }

    private <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        if (!latch.await(2, TimeUnit.SECONDS)) {
            return null;
        }
        return (T) data[0];
    }
}

// Internal Fake for Testing
class FakeWaterLevelRepository extends WaterLevelRepository {
    private final MutableLiveData<WaterLevel> waterLevelUpdates = new MutableLiveData<>();
    private final MutableLiveData<Exception> errorUpdates = new MutableLiveData<>();

    @Override
    public LiveData<WaterLevel> getWaterLevelUpdates() {
        return waterLevelUpdates;
    }

    @Override
    public LiveData<Exception> getErrorUpdates() {
        return errorUpdates;
    }

    public void pushData(WaterLevel data) {
        waterLevelUpdates.setValue(data);
    }

    @Override
    public void startMonitoring() {}

    @Override
    public void stopMonitoring() {}
}
