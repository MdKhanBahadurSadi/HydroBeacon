package com.sadi.hydrobeacon.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sadi.hydrobeacon.MainActivity;
import com.sadi.hydrobeacon.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DashboardInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testDashboard_isDisplayed() {
        // Check bottom navigation exists
        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()));
        
        // Check dashboard is selected by default
        onView(withId(R.id.navigation_dashboard)).check(matches(isSelected()));
    }

    @Test
    public void testBottomNav_switchToHistory() {
        // Click on History tab
        onView(withId(R.id.navigation_history)).perform(click());
        
        // Verify chart is displayed
        onView(withId(R.id.historyChart)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNav_switchToSettings() {
        // Click on Settings tab
        onView(withId(R.id.navigation_settings)).perform(click());
        
        // Verify slider is displayed
        onView(withId(R.id.sliderCaution)).check(matches(isDisplayed()));
    }
}
