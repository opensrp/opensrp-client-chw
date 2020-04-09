package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

@RunWith(OrderedRunner.class)
public class SideNavigationMenuTests {

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
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
        utils.openDrawer();
    }

    @Test
    @Order(order = 1)
    public void leftDrawerMenuResponsiveness() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring("Registers"))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 2)
    public void correctAppNameTest() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring(Constants.WcaroConfigs.appName))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 3)
    public void correctRegistersTest() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring(Constants.GenericConfigs.anc))
                .check(matches(isDisplayed()));
        onView(withSubstring(Constants.GenericConfigs.pnc))
                .check(matches(isDisplayed()));
        onView(withSubstring(Constants.GenericConfigs.child))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 4)
    public void syncTest() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring("Sync"))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 5)
    public void logOutUserName() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring("Log out as " + Constants.WcaroConfigs.wCaro_userName))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 6)
    public void changeLanguage() throws InterruptedException{
        Thread.sleep(500);
        onView(withSubstring("English"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Français"))
                .perform(click());
        onView(withSubstring("Tous les ménages"))
                .check(matches(isDisplayed()));
        utils.revertLanguage();
        utils.openDrawer();
    }
    @After
    public void tearDown() {
        utils.logOut();
    }

}
