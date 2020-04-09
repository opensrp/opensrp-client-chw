package org.smartregister.chw.activity.wcaro;

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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class EditTests {

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
        Thread.sleep(5000);
    }

    @Test
    public void editFamily() throws Throwable {
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Family details"))
                .perform(click());
        Thread.sleep(500);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:village_town")))
                .perform(clearText(), typeText("ThePlace"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Return to all families"))
                .check(matches(isDisplayed()));
    }
    @Test
    public void editFamilyMember() throws Throwable {
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName
                + " Family"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.AdditionalTestData.memberTwoFirstname
                + " " + Configs.AdditionalTestData.memberTwoSecondname + " " + Configs.TestConfigs.familyName
                + ", " + Configs.AdditionalTestData.extraMemberAge2))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Registration info"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:national_id")))
                .perform(clearText(), typeText(Configs.TestConfigs.nationalID));
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.AdditionalTestData.memberTwoFirstname
                + " " + Configs.AdditionalTestData.memberTwoSecondname + " " + Configs.TestConfigs.familyName
                + ", " + Configs.AdditionalTestData.extraMemberAge2))
                .check(matches(isDisplayed()));

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
