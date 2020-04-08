package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
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
import org.smartregister.chw.R;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.smartregister.chw.activity.utils.Utils.getViewId;

public class ChildVisitTests {

        @Rule
        public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

        @Rule
        public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

        @Rule
        public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

        @Rule
        public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

        Utils utils = new Utils();

        @Before
        public void setUp() throws InterruptedException {
            Thread.sleep(1000);
            utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
        }

        @Test
        public void registerChildVisit() throws Throwable {
            utils.openDrawer();
            onView(ViewMatchers.withSubstring(Constants.GenericConfigs.child))
                    .perform(click());
            onView(ViewMatchers.withHint("Search name or ID"))
                    .perform(typeText("Joan"), closeSoftKeyboard());
            onView(ViewMatchers.withSubstring("Joan Malea Cecil"))
                    .perform(click());
            onView(withId(R.id.textview_record_visit))
                    .perform(click());
            Thread.sleep(1000);
            childVaccine();
            immunizationsAtBirth();
            exclusiveBreastfeeding();
            birthCertification();
            earlyChildhoodDevelopment();
            llitn();
            illnessObservations();
            onView(ViewMatchers.withSubstring("SUBMIT"))
                    .perform(click());
            onView(ViewMatchers.withSubstring("View medical history"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }

        @Test
        public void confirmChildMedicakHistory() throws Throwable {
            utils.openDrawer();
            onView(ViewMatchers.withSubstring(Constants.GenericConfigs.child))
                    .perform(click());
            onView(ViewMatchers.withHint("Search name or ID"))
                    .perform(typeText("Joan"), closeSoftKeyboard());
            onView(ViewMatchers.withSubstring("Joan Malea Cecil"))
                    .perform(click());
            onView(ViewMatchers.withSubstring("View medical history"))
                    .perform(click());
            onView(withId(R.id.medical_history))
                    .check(matches(isDisplayed()));

        }

    public void childVaccine() throws Throwable{
        onView(ViewMatchers.withSubstring("Child vaccine card received"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
    }
    public void immunizationsAtBirth() throws Throwable{
        onView(ViewMatchers.withSubstring("Immunizations (at birth)"))
                .perform(click());
        onView(ViewMatchers.withSubstring("OPV 0"))
                .perform(click());
        onView(ViewMatchers.withSubstring("BCG"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
    }
    public void exclusiveBreastfeeding() throws Throwable{
        onView(ViewMatchers.withSubstring("Exclusive breastfeeding 0 months"))
                .perform(click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
    }
    public void birthCertification() throws Throwable{
        onView(ViewMatchers.withSubstring("Birth certification"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Yes"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert_issue_date")))
                .perform(click());
        onView(ViewMatchers.withSubstring("done"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert_num")))
                .perform(clearText(), typeText("7623456789"));
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }
    public void earlyChildhoodDevelopment() throws Throwable{
        onView(ViewMatchers.withSubstring("Early childhood development (ECD)"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:develop_warning_signs")))
                .perform(click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:stim_skills")))
                .perform(click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }
    public void llitn() throws Throwable{
        onView(ViewMatchers.withSubstring("Sleeping under a LLITN"))
                .perform(click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }
    public void illnessObservations() throws Throwable{
        onView(ViewMatchers.withSubstring("Observations & illness - optional"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:date_of_illness")))
                .perform(click());
        onView(ViewMatchers.withSubstring("done"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:illness_description")))
                .perform(clearText(), typeText("Coronavirus"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:action_taken")))
                .perform(click());
        onView(ViewMatchers.withSubstring("Managed"))
                .perform(click());
        onView(ViewMatchers.withSubstring("SAVE"))
                .perform(click());
    }

        @After
        public void completeTests(){
            mActivityTestRule.finishActivity();
        }

        public static Matcher<View> matchesDate(final int year, final int month, final int day) {
            return new BoundedMatcher<View, DatePicker>(DatePicker.class) {
                @Override
                public void describeTo(Description description) {
                description.appendText("matches date:");
                }

                @Override
                protected boolean matchesSafely(DatePicker item) {
                    return (year == item.getYear() && month == item.getMonth() && day == item.getDayOfMonth());
                }
            };
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
