package org.smartregister.chw.activity.ba;


import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

//import androidx.test.espresso.assertion.ViewAssertions;


@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class AddFamilyTestBA {

    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private Utils utils = new Utils();

    @Before
    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigUtils.ba_username, Constants.BoreshaAfyaConfigUtils.ba_password);
        Thread.sleep(5000);
    }

    @Test
    public void addFamily() throws Throwable {
        onView(withId(R.id.action_register))
                .perform(click());
        //Get activity
        Activity activity = getCurrentActivity();

        onView(withId(getViewId((JsonFormActivity) activity, "step1:fam_name")))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
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
        addFamilyMember();
        onView(ViewMatchers.withSubstring("SUBMIT"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(withId(R.id.action_family)).check(matches(isDisplayed()));

    }
    public void addFamilyMember() throws Throwable {
        Thread.sleep(1000);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step2:first_name")))
                .perform(doubleClick())
                .perform(typeText(Configs.TestConfigHelper.aboveFiveFirstNameOne), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step2:middle_name")))
                .perform(doubleClick())
                .perform(typeText(Configs.TestConfigHelper.aboveFiveSecondNameOne), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step2:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step2:age")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.aboveFiveage));
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("National ID"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step2:national_id")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.nationalID));
        onView(withId(getViewId((JsonFormActivity) activity, "step2:insurance_provider")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Community Health Fund (CHF)"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step2:insurance_provider_number")))
                .perform(scrollTo())
                .perform(typeText(Configs.TestConfigHelper.nationalID), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Sex"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Male"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Physical disabilities"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(withHint("Phone number")).perform(scrollTo())
                .perform(typeText("0721137816"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Farmer"))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Traditional leader"))
                .perform(scrollTo(), click());
        Thread.sleep(100);

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
