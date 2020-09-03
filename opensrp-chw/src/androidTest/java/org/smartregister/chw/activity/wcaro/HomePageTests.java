package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(OrderedRunner.class)
public class HomePageTests {
    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private Utils utils = new Utils();

    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        utils.logIn(Constants.WcaroConfigUtils.wCaro_username, Constants.WcaroConfigUtils.wCaro_password);
    }
    @Test
    public void searchByName() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Constants.WcaroConfigUtils.searchFamilyWCaro), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Constants.WcaroConfigUtils.familyWcaro))
                .check(matches(isDisplayed()));
        Thread.sleep(1000);
        utils.openDrawer();
        utils.logOut();
    }

    @Test
    public void searchByID() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Constants.WcaroConfigUtils.searchFamilyIDWCaro), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Constants.WcaroConfigUtils.familyWcaro))
                .check(matches(isDisplayed()));
        Thread.sleep(1000);
        utils.openDrawer();
        utils.logOut();
    }

    @Test
    public void checkJobAids() {
        onView(withId(R.id.action_job_aids))
                .check(matches(isDisplayed()));
        utils.openDrawer();
        utils.logOut();
    }



}
