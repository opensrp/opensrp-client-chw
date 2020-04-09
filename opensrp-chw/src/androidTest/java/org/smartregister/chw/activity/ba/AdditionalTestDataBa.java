package org.smartregister.chw.activity.ba;

import android.app.Activity;

import androidx.annotation.StringRes;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class AdditionalTestDataBa {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    Utils utils = new Utils();

    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
    }


    @Test
    public void test() {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
    }

    @After
    public void completeTests() throws Throwable{
        utils.addTestFamilyMemberBa(Configs.AdditionalTestData.memberOneFirstname,
                Configs.AdditionalTestData.memberOneSecondname,
                Configs.AdditionalTestData.extraMemberAge1);
        utils.addTestFamilyMemberBa(Configs.AdditionalTestData.memberTwoFirstname,
                Configs.AdditionalTestData.memberTwoSecondname,
                Configs.AdditionalTestData.extraMemberAge2);
        /*utils.addTestFamilyMemberBa(Configs.AdditionalTestData.memberThreeFirstname,
                Configs.AdditionalTestData.memberThreeSecondname,
                Configs.AdditionalTestData.extraMemberAge3);
         */
        mActivityTestRule.finishActivity();
    }

    private String getString(@StringRes int resourceId) {
        return mActivityTestRule.getActivity().getString(resourceId);
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
