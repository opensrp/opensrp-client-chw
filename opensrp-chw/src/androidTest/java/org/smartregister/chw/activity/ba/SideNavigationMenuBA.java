package org.smartregister.chw.activity.ba;


import android.Manifest;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(OrderedRunner.class)
public class SideNavigationMenuBA {


    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);
    //@Rule
    //public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    Utils utils = new Utils();

    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
        utils.openDrawer();
    }

    @Test
    @Order(order = 1)
    public void leftDrawerMenuResponsiveness() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Registers"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 2)
    public void correctAppNameTest() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.appName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 3)
    public void correctRegistersTest() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.pnc))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.child))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.family_planning))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.GenericConfigs.malaria))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 4)
    public void syncTest() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Sync"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 5)
    public void logOutUserName() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Log out as " + Constants.BoreshaAfyaConfigs.ba_userName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


    @Test
    @Order(order = 6)
    public void changeLanguage() throws InterruptedException{
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("English"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Kiswahili"))
                .perform(click());
        onView(withId(R.id.txt_title_label)).check(matches(isDisplayed()));
        utils.revertLanguageSwahili();
        utils.openDrawer();
    }
    @After
    public void tearDown() throws InterruptedException{
        utils.logOutBA();
    }
}
