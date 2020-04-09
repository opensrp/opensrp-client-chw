package org.smartregister.chw.activity.ba;


import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

//import org.junit.Before;

@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class AddFamilyFailTestsBa {
    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private Utils utils = new Utils();


    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
    }

    @Test
    @Order(order = 1)
    public void addFamilyWithoutAllfields() throws Throwable{
        onView(withId(R.id.action_register)).perform(click());
        Thread.sleep(1000);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:nearest_facility")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Kabila Village"))
                .perform(doubleClick());
        onView(ViewMatchers.withSubstring("Next"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step2:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step2:age")))
                .perform(scrollTo(), typeText(Configs.TestConfigs.aboveFiveage));
        onView(ViewMatchers.withSubstring("SUBMIT"))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Found 11 error(s) in the form. Please correct them to submit."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 2)
    public void confirmUniqueID() throws Throwable {
        onView(withId(R.id.action_register))
                .perform(click());
        //Get activity
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:fam_name")))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:fam_village")))
                .perform(typeText("One"), closeSoftKeyboard());

        onView(withId(getViewId((JsonFormActivity) activity, "step1:landmark")))
                .perform(typeText("Fig tree 5 meters North west of the house"), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:gps")))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("OK"))
                .perform(click());
        Thread.sleep(100);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:nearest_facility")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Kabila Village"))
                .perform(doubleClick());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Next"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step2:unique_id")))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @After
    public void completeTests(){
        intentsTestRule.finishActivity();
    }


    private Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        runOnUiThread(() -> {
            java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activities);
        });
        return activity[0];
    }

}
