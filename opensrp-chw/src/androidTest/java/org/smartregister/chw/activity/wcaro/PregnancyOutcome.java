package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;

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
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

public class PregnancyOutcome {
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
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }

    public void registerANCOutcome()throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withContentDescription("More options")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Pregnancy outcome"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:preg_outcome")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Live birth"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:delivery_date")))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:delivery_place")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Home"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:no_children_no")))
                .perform(clearText(), typeText("1"));
        utils.floatingButton().perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(scrollTo(), clearText(), typeText("Other"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(scrollTo(), clearText(), typeText("Other1"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:gender")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Male"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:lbw")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:early_bf_1hr")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:disabilities")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:opv0_date")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:bcg_date")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);

    }

    public void registerNewborn() throws Throwable{
        Thread.sleep(500);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:same_as_fam_name")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(scrollTo(), clearText(), typeText("Other"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(scrollTo(), clearText(), typeText("Other1"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:gender")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Male"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:lbw")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:early_bf_1hr")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:disabilities")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:opv0_date")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:bcg_date")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
    }

    @Test
    public void registerAMiscarriage() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withContentDescription("More options")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Pregnancy outcome"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:preg_outcome")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Miscarriage"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:miscarriage_date")))
                .perform(click());
        onView(ViewMatchers.withSubstring("done"))
                .perform(click());
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }

    @Test
    public void registerStillbirth() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withContentDescription("More options")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Pregnancy outcome"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:preg_outcome")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Stillbirth"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:delivery_date")))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Home"))
                .perform(click());
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }

    @Test
    public void registerOtherPregnancyOutcome() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withContentDescription("More options")).perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Pregnancy outcome"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:preg_outcome")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Other"))
                .perform(click());
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
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
