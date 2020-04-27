package org.smartregister.chw.activity.wcaro;

import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

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
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
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

        private Utils utils = new Utils();

        @Before
        public void setUp() throws InterruptedException {
            Thread.sleep(1000);
            utils.logIn(Constants.WcaroConfigUtils.wCaro_username, Constants.WcaroConfigUtils.wCaro_password);
        }

        @Test
        public void registerChildVisit() throws Throwable {
            utils.openDrawer();
            onView(withSubstring(Constants.GenericConfigUtils.child))
                    .perform(click());
            onView(withHint("Search name or ID"))
                    .perform(typeText("Joan"), closeSoftKeyboard());
            onView(withSubstring("Joan Malea Cecil"))
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
            onView(withSubstring("SUBMIT"))
                    .perform(click());
            onView(withSubstring("View medical history"))
                    .check(matches(isDisplayed()));
        }

        @Test
        public void confirmChildMedicakHistory() {
            utils.openDrawer();
            onView(withSubstring(Constants.GenericConfigUtils.child))
                    .perform(click());
            onView(withHint("Search name or ID"))
                    .perform(typeText("Joan"), closeSoftKeyboard());
            onView(withSubstring("Joan Malea Cecil"))
                    .perform(click());
            onView(withSubstring("View medical history"))
                    .perform(click());
            onView(withId(R.id.medical_history))
                    .check(matches(isDisplayed()));

        }

    private void childVaccine() throws Throwable{
        onView(withSubstring("Child vaccine card received"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("No"))
                .perform(click());
        onView(withSubstring("Save"))
                .perform(click());
    }
    private void immunizationsAtBirth() throws Throwable{
        onView(withSubstring("Immunizations (at birth)"))
                .perform(click());
        onView(withSubstring("OPV 0"))
                .perform(click());
        onView(withSubstring("BCG"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Save"))
                .perform(click());
    }
    private void exclusiveBreastfeeding() throws Throwable{
        onView(withSubstring("Exclusive breastfeeding 0 months"))
                .perform(click());
        onView(withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("Save"))
                .perform(click());
    }
    private void birthCertification() throws Throwable{
        onView(withSubstring("Birth certification"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert")))
                .perform(click());
        onView(withSubstring("Yes"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert_issue_date")))
                .perform(click());
        onView(withSubstring("done"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:birth_cert_num")))
                .perform(clearText(), typeText("7623456789"));
        onView(withSubstring("SAVE"))
                .perform(click());
    }
    private void earlyChildhoodDevelopment() throws Throwable{
        onView(withSubstring("Early childhood development (ECD)"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:develop_warning_signs")))
                .perform(click());
        onView(withSubstring("No"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:stim_skills")))
                .perform(click());
        onView(withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(withSubstring("SAVE"))
                .perform(click());
    }
    private void llitn(){
        onView(withSubstring("Sleeping under a LLITN"))
                .perform(click());
        onView(withSubstring("No"))
                .perform(click());
        onView(withSubstring("SAVE"))
                .perform(click());
    }
    private void illnessObservations() throws Throwable{
        onView(withSubstring("Observations & illness - optional"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:date_of_illness")))
                .perform(click());
        onView(withSubstring("done"))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:illness_description")))
                .perform(clearText(), typeText("Coronavirus"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:action_taken")))
                .perform(click());
        onView(withSubstring("Managed"))
                .perform(click());
        onView(withSubstring("SAVE"))
                .perform(click());
    }

        @After
        public void completeTests(){
            mActivityTestRule.finishActivity();
        }


        private Activity getCurrentActivity() throws Throwable {
            getInstrumentation().waitForIdleSync();
            final Activity[] activity = new Activity[1];
            runOnUiThread(() -> {
                java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activities);
            });
            return activity[0];
        }

    }
