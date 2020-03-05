package org.smartregister.chw.activity.wcaro;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;
import org.smartregister.family.activity.FamilyWizardFormActivity;

import static androidx.databinding.adapters.TextViewBindingAdapter.setText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class ANCRegisterTests {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    public ActivityTestRule<FamilyWizardFormActivity> mActivityTestRule2 = new ActivityTestRule<>(FamilyWizardFormActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    Utils utils = new Utils();

    public void setUp() throws InterruptedException {
        Thread.sleep(10000);
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }

    @Test
    @Order(order = 1)
    public void searchANCRecord() throws Throwable {
        utils.openDrawer();
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    @Order(order = 2)
    public void confirmANCRecordProfile() throws Throwable {
        utils.openDrawer();
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName + ", "+ Configs.TestConfigs.aboveFiveage))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 3)
    public void confirmANCVisitPage() throws Throwable {
        utils.openDrawer();
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withId(R.id.textview_record_visit))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName + ", "+ Configs.TestConfigs.aboveFiveage + " · ANC Visit"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @After
    public void completeTests() {
        mActivityTestRule.finishActivity();
    }

    Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activities);
            }
        });
        return activity[0];
    }
}
