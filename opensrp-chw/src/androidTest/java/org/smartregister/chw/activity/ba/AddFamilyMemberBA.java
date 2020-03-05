package org.smartregister.chw.activity.ba;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;


@LargeTest
//@RunWith(AndroidJUnit4.class)
@RunWith(OrderedRunner.class)
public class AddFamilyMemberBA {

    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    Utils utils = new Utils();

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof EditText) {
                    return ((EditText) item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message" + expected + ", find it!");
            }
        };
    }

    @Before
    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
        Thread.sleep(5000);
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
    @Test
    public void addFamilyMember() throws Throwable {
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("TestFamilys Family"))
                .perform(click());
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Child under 5 years"))
                .perform(click());
        Thread.sleep(1000);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:surname")))
                .perform(typeText("TestFamily"), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(typeText("OneName"), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(doubleClick())
                .perform(typeText("TwoName"), closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob")))
                .perform(scrollTo(), doubleClick());
        Thread.sleep(1000);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("done"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Health insurance provider"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Jubilee Insurance"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:insurance_provider_number")))
                .perform(scrollTo())
                .perform(typeText("1234567893"), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Sex"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Female"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Physical disabilities"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Save"))
                .perform(click());
    }

    @After
    public void tearDown() throws Throwable{
        Thread.sleep(1000);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Return to all families"))
                .perform(click());
        Thread.sleep(5000);
    }


}
