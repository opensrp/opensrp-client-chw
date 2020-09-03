package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
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
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

public class AddFamilyMemberTest {

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
        utils.logIn(Constants.WcaroConfigUtils.wCaro_username, Constants.WcaroConfigUtils.wCaro_password);
    }


    @Test
    public void addFamilyMemberAboveFive() throws Throwable {
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(withSubstring("Other family member"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:national_id")))
                .perform(typeText(Configs.TestConfigHelper.nationalID));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:same_as_fam_name")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.aboveFiveFirstNameTwo));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.aboveFiveSecondNameTwo));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:age")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.aboveFiveage));
        onView(withSubstring("Sex"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(withSubstring("Female"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Save"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("MEMBERS"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void addFamilyWithBlankFields() throws Throwable{
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(withSubstring("Other family member"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:age")))
                .perform(scrollTo(), typeText(Configs.TestConfigHelper.aboveFiveage));
        onView(withSubstring("Save"))
                .perform(click());
        onView(withSubstring("Found 3 error(s) in the form. Please correct them to submit."))
                .check(matches(isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    public void confirmUniqueIDPrepopulated() throws Throwable {
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(withSubstring("Other family member"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:unique_id")))
                .check(matches(isDisplayed()));
        Thread.sleep(500);

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
