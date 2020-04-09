package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
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
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;


@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class WashCheckVisitTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    private Utils utils = new Utils();


    public void setUp() throws InterruptedException{

        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }

    @Test
    @Order(order = 1)
    public void washCheckVisitTest() throws Throwable{
        onView(withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withSubstring("DUE"))
                .perform(click());
        Thread.sleep(500);
        utils.locateLayout(0,1).perform(click());
        Thread.sleep(200);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:handwashing_facilities")))
                .perform(scrollTo(), click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:drinking_water")))
                .perform(scrollTo(), click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:hygienic_latrine")))
                .perform(scrollTo(), click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }

    @Test
    @Order(order = 2)
    public void confirmWashCheck() throws Throwable {
        onView(withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withSubstring("ACTIVITY"))
                .perform(click());
        Thread.sleep(500);
        utils.locateLayout(0,1).perform(click());
        Thread.sleep(500);
        onView(withSubstring("WASH check"))
                .check(matches(isDisplayed()));

    }
    @Test
    @Order(order = 3)
    public void confirmActivityPage() throws Throwable {
        onView(withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withSubstring("ACTIVITY"))
                .perform(click());
        Thread.sleep(500);
        utils.locateLayout(0,1).perform(click());
        Thread.sleep(500);
        onView(withSubstring("WASH check"))
                .check(matches(isDisplayed()));

    }
    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
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
