package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Order;
import org.smartregister.chw.activity.utils.OrderedRunner;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class ANCRegisterTests {
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

    @Test
    @Order(order = 1)
    public void searchANCRecord() {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .check(ViewAssertions.matches(isDisplayed()));
    }
    @Test
    @Order(order = 2)
    public void confirmANCRecordProfile()  {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    @Order(order = 3)
    public void confirmANCVisitPage()  {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withId(R.id.textview_record_visit))
                .perform(click());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName + ", "+ Configs.TestConfigs.aboveFiveage + " · ANC Visit"))
                .check(ViewAssertions.matches(isDisplayed()));
    }
    @Test
    @Order(order = 4)
    public void confirmANCSearchWorks() {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    public void confirmANCPhoneNumber() throws Throwable {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText("Jkk"), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring("Jkk Fgh"))
                .perform(click());
        Thread.sleep(500);
        //utils.ancFloatingfab()
                //.perform(click());
        onView(ViewMatchers.withSubstring("Call"))
                .check(ViewAssertions.matches(isDisplayed()));
    }
    @Test
    @Order(order = 5)
    public void testANCProfileView() {
        utils.openDrawer();
        onView(ViewMatchers.withSubstring(Constants.GenericConfigs.anc))
                .perform(click());
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo))
                .perform(click());
        onView(ViewMatchers.withSubstring("Return to all ANC women"))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @After
    public void completeTests() {
        mActivityTestRule.finishActivity();
    }

}
