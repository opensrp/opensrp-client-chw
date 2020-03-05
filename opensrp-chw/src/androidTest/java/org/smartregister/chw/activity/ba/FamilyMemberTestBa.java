package org.smartregister.chw.activity.ba;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class FamilyMemberTestBa {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    Utils utils = new Utils();

    @Before
    public void setUp() throws InterruptedException{

        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
        Thread.sleep(5000);
    }

    @Test
    public void removefamilyMemberSuccessfully() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Reason for removal *"))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Moved away"))
                .perform(click());
        onView(withHint("Date moved away *"))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("done"))
                .perform(click());
        onView(withId(R.id.action_save))
                .check(matches(isDisplayed()));//perform(click());
    }

    @Test
    public void removefamilyMemberWithoutReason() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Found 1 error(s) in the form. Please correct them to submit."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.action_bar_root))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void changeFamilyHeadsuccessfully() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Change family head"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.etPhoneNumber)).check(matches(isDisplayed()));
        //onView(withHint("Phone number")).perform(typeText("+254721212122"));
        //onView(withHint("Other phone number")).perform(typeText("+254721212121"));
        //onView(withId(R.id.tvAction))
                //.check(matches(isDisplayed()));//(click());
    }

    @Test
    public void changePrimarycareGiverSuccessfully() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Change primary caregiver"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
    }

    @Test
    public void confirmFamilyHead() throws InterruptedException {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Eliud Kipchoge, 67"))
                .perform(click());
        onView(withId(R.id.family_head))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmUniqueId() throws InterruptedException {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Eliud Kipchoge, 67"))
                .perform(click());
        onView(withId(R.id.textview_detail_three))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmFabOptions() throws InterruptedException {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.fab))
                .perform(click());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Call"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Add new family member"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
