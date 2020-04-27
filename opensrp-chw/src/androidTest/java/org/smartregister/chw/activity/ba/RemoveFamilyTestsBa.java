package org.smartregister.chw.activity.ba;

import androidx.test.rule.ActivityTestRule;

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
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

//import org.junit.Before;

@RunWith(OrderedRunner.class)
public class RemoveFamilyTestsBa {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    public void setUp() throws InterruptedException{
        utils.logIn(Constants.BoreshaAfyaConfigUtils.ba_username, Constants.BoreshaAfyaConfigUtils.ba_password);
        Thread.sleep(5000);
    }

    @Test
    @Order(order = 8)
    public void removeFamilyWithoutReason() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Removing entire family"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.action_save))
                .perform(click());
        onView(withSubstring("Found 1 error(s) in the form. Please correct them to submit."))
                .check(matches(isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    @Order(order = 9)
    public void removeFamily() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Removing entire family"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Reason for closure *"))
                .perform(click());
        onView(withSubstring("Relocation"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Household relocation *"))
                .perform(click());
        onView(withSubstring("Outside the district"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("REMOVE"))
                .perform(click());
        Thread.sleep(200);
    }

    @Order(order = 5)
    public void changeFamilyHeadsuccessfully() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Change family head"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameTwo + " " + Configs.TestConfigHelper.familyName + ", "
                + Configs.TestConfigHelper.aboveFiveage))
                .perform(click());
        Thread.sleep(2000);
        onView(withHint("Phone number")).
                perform(clearText(), typeText(Configs.TestConfigHelper.phoneNumberOne));
        onView(withHint("Other phone number"))
                .perform(clearText(), typeText(Configs.TestConfigHelper.getPhoneNumberTwo));
        onView(withSubstring("SAVE"))
                .perform(click());
    }

    @Order(order = 4)
    public void changePrimarycareGiverSuccessfully() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Change primary caregiver"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameTwo + " " + Configs.TestConfigHelper.familyName + ", "
                + Configs.TestConfigHelper.aboveFiveage))
                .perform(click());
        Thread.sleep(2000);
        onView(withHint("Phone number")).perform(clearText(), typeText(Configs.TestConfigHelper.phoneNumberOne));
        onView(withHint("Other phone number"))
                .perform(clearText(), typeText(Configs.TestConfigHelper.getPhoneNumberTwo));
        onView(withSubstring("SAVE"))
                .perform(click());
    }

    @Test
    @Order(order = 1)
    public void confirmFamilyHead() throws InterruptedException {
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameOne
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameOne + ", "
                + Configs.TestConfigHelper.aboveFiveage))
                .perform(click());
        onView(withId(R.id.family_head))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 2)
    public void confirmUniqueId() throws InterruptedException {
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring(Configs.TestConfigHelper.aboveFiveFirstNameOne
                + " " + Configs.TestConfigHelper.aboveFiveSecondNameOne + ", "
                + Configs.TestConfigHelper.aboveFiveage))
                .perform(click());
        onView(withId(R.id.textview_detail_three))
                .check(matches(isDisplayed()));
    }

    @Test
    @Order(order = 3)
    public void confirmFabOptions() throws InterruptedException {
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.fab))
                .perform(click());
        onView(withSubstring("Call"))
                .check(matches(isDisplayed()));
        onView(withSubstring("Add new family member"))
                .check(matches(isDisplayed()));
    }

    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
    }
}
