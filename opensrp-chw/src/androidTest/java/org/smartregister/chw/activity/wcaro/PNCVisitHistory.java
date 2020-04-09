package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;

public class PNCVisitHistory {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private Utils utils = new Utils();

    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(1000);
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }

    @Test
    public void viewPNCVisitHistory() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.pnc))
                .perform(click());
        onView(ViewMatchers.withHint("Find name or ID"))
                .perform(typeText("Ana"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Ana AnotherSixqrw, 23"))
                .perform(click());
        onView(ViewMatchers.withSubstring("View medical history"))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withSubstring("Medical History"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
    }

}
