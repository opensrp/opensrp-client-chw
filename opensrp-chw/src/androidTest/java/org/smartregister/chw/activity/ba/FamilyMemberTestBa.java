package org.smartregister.chw.activity.ba;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

public class FamilyMemberTestBa {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    public void setUp() throws InterruptedException{

        utils.logIn(Constants.BoreshaAfyaConfigUtils.ba_username, Constants.BoreshaAfyaConfigUtils.ba_password);
        Thread.sleep(5000);
    }

    @Test
    public void removefamilyMemberSuccessfully() throws InterruptedException{
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withSubstring("Reason for removal *"))
                .perform(click());
        onView(withSubstring("Moved away"))
                .perform(click());
        onView(withHint("Date moved away *"))
                .perform(click());
        onView(withSubstring("done"))
                .perform(click());
        onView(withId(R.id.action_save))
                .check(matches(isDisplayed()));//perform(click());
    }

    @Test
    public void removefamilyMemberWithoutReason() throws InterruptedException{
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
        onView(withSubstring("Found 1 error(s) in the form. Please correct them to submit."))
                .check(matches(isDisplayed()));
        onView(withId(R.id.action_bar_root))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void changeFamilyHeadsuccessfully() throws InterruptedException{
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Change family head"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.etPhoneNumber)).check(matches(isDisplayed()));
        //onView(withHint("Phone number")).perform(typeText("+254721212122"));
        //onView(withHint("Other phone number")).perform(typeText("+254721212121"));
        //onView(withId(R.id.tvAction))
                //.check(matches(isDisplayed()));//(click());
    }

    @Test
    public void changePrimarycareGiverSuccessfully() throws InterruptedException{
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(withSubstring("Change primary caregiver"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Merab Emerald Nandi, 26"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
    }

    @Test
    public void confirmFamilyHead() throws InterruptedException {
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Eliud Kipchoge, 67"))
                .perform(click());
        onView(withId(R.id.family_head))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmUniqueId() throws InterruptedException {
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Eliud Kipchoge, 67"))
                .perform(click());
        onView(withId(R.id.textview_detail_three))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmFabOptions() throws InterruptedException {
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.fab))
                .perform(click());
        onView(withSubstring("Call"))
                .check(matches(isDisplayed()));
        onView(withSubstring("Add new family member"))
                .check(matches(isDisplayed()));
    }
}
