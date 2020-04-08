package org.smartregister.chw.activity.ba;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
//import org.junit.Before;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;


@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class RemoveMemberTestsBa {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    Utils utils = new Utils();

    public void setUp() throws InterruptedException{
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
        Thread.sleep(5000);
    }

    @Test
    @Order(order = 2)
    public void confirmWarningWidgetWhenRemovingMember() throws Throwable{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.AdditionalTestData.memberOneFirstname
                + " " +
                Configs.AdditionalTestData.memberOneSecondname + " " + Configs.TestConfigs.familyName
                + ", " + Configs.AdditionalTestData.extraMemberAge1))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:remove_reason")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Other"))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("REMOVE"))
                .perform(click());
        Thread.sleep(500);
    }

    @Test
    @Order(order = 6)
    public void removeFamilyMemberWithOtherAsReason() throws Throwable{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.AdditionalTestData.memberTwoFirstname
                + " " + Configs.AdditionalTestData.memberTwoSecondname + " "
                + Configs.TestConfigs.familyName
                + ", " + Configs.AdditionalTestData.extraMemberAge2))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:remove_reason")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Moved away"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:date_moved")))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.action_save))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("REMOVE"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    @Order(order = 5)
    public void removeFamilyMemberWithDeathAsReason() throws Throwable {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.kidNameFirst
                + " " + Configs.TestConfigs.kidNameSecond + " " + Configs.TestConfigs.familyName + ", 0"))
                .perform(click());
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:remove_reason")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Died"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:date_died")))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("done")).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.action_save))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("REMOVE"))
                .perform(click());
        Thread.sleep(500);
    }

    @Test
    @Order(order = 1)
    public void removeFamilyMemberWithoutReason() throws InterruptedException{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.AdditionalTestData.memberTwoFirstname
                + " " + Configs.AdditionalTestData.memberTwoSecondname + " " + Configs.TestConfigs.familyName
                + ", " + Configs.AdditionalTestData.extraMemberAge2))
                .perform(click());
        onView(withId(R.id.action_save))
                .perform(click());
        onView(ViewMatchers.withSubstring("Found 1 error(s) in the form. Please correct them to submit."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Order(order = 4)
    @Test
    public void confirmCaregiverReplacementBeforeRemoval() throws InterruptedException {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameTwo
                + " " + Configs.TestConfigs.aboveFiveSecondNameTwo + " " + Configs.TestConfigs.familyName
                + ", " + Configs.TestConfigs.aboveFiveage))
                .perform(click());
        onView(ViewMatchers.withSubstring("Before you remove this member " +
                "you must select a new primary caregiver"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @Test
    @Order(order = 3)
    public void confirmFamilyHeadReplacementBeforeRemoval() throws InterruptedException {
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
        Thread.sleep(500);
        utils.openFamilyMenu();
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Remove existing family member"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.aboveFiveFirstNameOne
                + " " + Configs.TestConfigs.aboveFiveSecondNameOne
                + ", " + Configs.TestConfigs.aboveFiveage))
                .perform(click());
        onView(ViewMatchers.withSubstring("Before you remove this member " +
                "you must select a new family head."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(500);
    }

    @After
    public void completeTests(){
        mActivityTestRule.finishActivity();
    }

    private String getString(@StringRes int resourceId) {
        return mActivityTestRule.getActivity().getString(resourceId);
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof EditText) {
                    return ((EditText)item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message" + expected + ", find it!");
            }
        };
    }

    Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activities);
            }
        });
        return activity[0];
    }
}
