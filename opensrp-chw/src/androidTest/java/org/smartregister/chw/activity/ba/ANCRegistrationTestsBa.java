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
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

//import org.junit.Before;

@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class ANCRegistrationTestsBa {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private Utils utils = new Utils();

    public void setUp() throws InterruptedException {
        Thread.sleep(10000);
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
    }

    @Test
    @Order(order = 2)
    public void registerANCSuccessfuly() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        /*onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName + ", "
                + Configs.TestConfigs.aboveFiveage))
                .perform(click());*/
        utils.locateLayout(1,2).perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("ANC Registration"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:last_menstrual_period_unknown")))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:edd")))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:no_prev_preg")))
                .perform(typeText("0"));
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:phone_number")))
                .perform(scrollTo(), clearText(), typeText("0882454545"));
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:marital_status")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Co-habiting"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:person_assist")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        onView(ViewMatchers.withSubstring("ANC Clients"))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    @Order(order = 1)
    public void registerANCWithoutAllFields() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        /*onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName + ", "
                + Configs.TestConfigs.aboveFiveage))
                .perform(click());*/
        utils.locateLayout(1,2).perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("ANC Registration"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:last_menstrual_period")))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Found 3 error(s) in the form. Please correct them to submit."))
                .check(ViewAssertions.matches(isDisplayed()));
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
