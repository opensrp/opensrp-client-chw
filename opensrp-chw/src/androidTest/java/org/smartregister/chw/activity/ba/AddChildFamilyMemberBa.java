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
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
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
public class AddChildFamilyMemberBa {

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
        Thread.sleep(5000);
    }

    @Test
    public void addChildFamilyMember() throws Throwable {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Child under 5 years"))
                .perform(click());
        Thread.sleep(100);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:same_as_fam_name")))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(typeText(Configs.TestConfigs.kidNameFirst), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(doubleClick())
                .perform(typeText(Configs.TestConfigs.kidNameSecond), closeSoftKeyboard());
        Thread.sleep(100);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:insurance_provider")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Community Health Fund (CHF)"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:insurance_provider_number")))
                .perform(scrollTo())
                .perform(typeText(Configs.TestConfigs.nationalID), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:gender")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Male"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:disabilities")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert_available")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Yes"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_regist_number")))
                .perform(scrollTo(), typeText("234567890"));
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:rhc_card")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .check(ViewAssertions.matches(isDisplayed()));
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("MEMBERS"))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void addFamilyWithBlankFieldsBa() throws Throwable {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Child under 5 years"))
                .perform(click());
        Thread.sleep(100);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Found 7 error(s) in the form. Please correct them to submit."))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void confirmUniqueIDPrepopulatedBa() throws Throwable {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Child under 5 years"))
                .perform(click());
        Thread.sleep(100);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:unique_id")))
                .check(ViewAssertions.matches(isDisplayed()));
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
