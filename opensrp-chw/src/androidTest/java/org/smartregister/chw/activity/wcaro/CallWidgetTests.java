package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CallWidgetTests {
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
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }
    @Test
    public void confirmCallOption() throws Throwable {
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Call"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    public void confirmCallCareGiverPage() throws Throwable {
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Call caregiver"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);

    }
    @Test
    public void confirmPhoneNumber() throws Throwable{
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.phoneNumberOne))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    public void confirmFullFamilyHeadNames() throws Throwable {
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Call"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameOne
                + " " + Configs.TestConfigs.aboveFiveSecondNameOne + " " + Configs.TestConfigs.familyName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
    }

}
