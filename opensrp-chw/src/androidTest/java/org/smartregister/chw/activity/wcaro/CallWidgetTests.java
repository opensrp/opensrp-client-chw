package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;
import org.smartregister.family.activity.FamilyWizardFormActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class CallWidgetTests {
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
    public void confirmCallOption() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    public void confirmCallCareGiverPage() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call caregiver"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);

    }
    @Test
    public void confirmPhoneNumber() throws Throwable{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.phoneNumberOne))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    public void confirmFullFamilyHeadNames() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameOne
                + " " + Configs.TestConfigs.aboveFiveSecondNameOne + " " + Configs.TestConfigs.familyName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
    }

    private String getString(@StringRes int resourceId) {
        return mActivityTestRule.getActivity().getString(resourceId);
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof EditText) {
                    return ((EditText)item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message" + expected + ", find it!");
            }
        };
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
