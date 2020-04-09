package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

public class PNCVisit {
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
        Thread.sleep(1000);
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }

    @Test
    public void confirmPNCVisit() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.pnc))
                .perform(click());
        onView(ViewMatchers.withHint("Find name or ID"))
                .perform(typeText("Ii"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Ii Gg"))
                .perform(click());
        onView(withId(R.id.textview_record_visit))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withSubstring("Danger signs - mother"))
                .check(matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void successfullyRecordPNCVisit() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.pnc))
                .perform(click());
        onView(ViewMatchers.withHint("Find name or ID"))
                .perform(typeText("Ii"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Ii Gg"))
                .perform(click());
        onView(withId(R.id.textview_record_visit))
                .perform(click());
        Thread.sleep(1000);
        //dangerSigns();
        //healthFacilityVisit();
        //familyPlanning();
        //observationsAndIllness();
    }

    private void dangerSigns() {
        onView(ViewMatchers.withSubstring("Danger signs - mother"))
                .perform(click());
        onView(ViewMatchers.withSubstring("None"))
                .perform(click());
        onView(withId(R.id.next)).perform(click());
    }

    private void healthFacilityVisit() {
        onView(ViewMatchers.withSubstring("PNC health facility visit - day 7"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Did the woman attend her PNC visit (Day 7) at the health facility?"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Yes"))
                .perform(click());
        onView(withId(R.id.next)).perform(click());
    }

    private void familyPlanning() {
        onView(ViewMatchers.withSubstring("Family planning"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Woman counseled on Family Planning? *"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Yes"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Family Planning method chosen? *"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Yes"))
                .perform(click());
        onView(withId(R.id.next)).perform(click());
    }

    private void observationsAndIllness() throws Throwable{
        onView(ViewMatchers.withSubstring("Observations & illness - mother -"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:date_of_illness")))
                .perform(click());
        onView(ViewMatchers.withSubstring("done"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:illness_description")))
                .perform(clearText(), typeText("Coronavirus"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:action_taken")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Managed"))
                .perform(click());
        onView(withId(R.id.next)).perform(click());
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
