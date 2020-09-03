package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;


@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class ANCVisitTests {
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
        utils.logIn(Constants.WcaroConfigUtils.wCaro_username, Constants.WcaroConfigUtils.wCaro_password);
    }

    @Order(order = 1)
    @Test
    public void testAncVisit() throws Throwable {
        utils.openDrawer();
        onView(withSubstring(Constants.GenericConfigUtils.anc))
                .perform(click());
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withId(R.id.textview_record_visit))
                .perform(click());
        Thread.sleep(1000);
        dangerSigns();
        ancCounselling();
        sleepingUnderLLITN();
        testANCCardReceived();
        testANCHealthFacilityVisit();
        //testIPTpSPDose();
        Thread.sleep(1000);
        onView(withSubstring("SUBMIT"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("View medical history"))
                .check(matches(isDisplayed()));

    }

    @Test
    @Order(order = 2)
    public void viewAncMedicalHistory() throws Throwable {
        utils.openDrawer();
        onView(withSubstring(Constants.GenericConfigUtils.anc))
                .perform(click());
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.aboveFiveFirstNameTwo), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameTwo))
                .perform(click());
        onView(withSubstring("View medical history"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Medical History"))
                .check(matches(isDisplayed()));
    }

    public void dangerSigns() throws Throwable{
        onView(withSubstring("Danger signs"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("None"))
                .perform(click());
        onView(withSubstring("Counseling on seeking care" +
                " immediately for danger signs *"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }
    public void ancCounselling() throws Throwable{
        onView(withSubstring("ANC counseling"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Importance of ANC visits *"))
                .perform(click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withSubstring("Importance of delivering at a health facility *"))
                .perform(click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withSubstring("Nutrition counseling *"))
                .perform(click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }
    public void sleepingUnderLLITN() throws Throwable{
        onView(withSubstring("Sleeping under a LLITN"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("No"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }

    public void testANCCardReceived() throws Throwable{
        onView(withSubstring("ANC card received"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("No"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }

    public void testANCHealthFacilityVisit() throws Throwable{
        onView(withSubstring("ANC 1 health facility visit"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Did the woman attend her " +
                "ANC 1 visit at the health facility"))
                .perform(click());
        onView(withSubstring("No"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }

    public void testIPTpSPDose() throws Throwable{
        onView(withSubstring("IPTp-SP dose 1"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Save"))
                .perform(click());
    }

}
