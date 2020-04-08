package org.smartregister.chw.activity.ba;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Assert;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(OrderedRunner.class)
public class HomePageTestsBa {
    @Rule
    public ActivityTestRule<LoginActivity> intentsTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule1 = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    Utils utils = new Utils();

    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);



    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigs.ba_username, Constants.BoreshaAfyaConfigs.ba_password);
    }
    @Test
    @Order(order = 1)
    public void searchByName() throws InterruptedException{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigs.familyName), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    @Order(order = 2)
    public void searchByID() throws InterruptedException{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Constants.BoreshaAfyaConfigs.searchFamilyIDBa), closeSoftKeyboard());
        onView(ViewMatchers.withSubstring(Constants.BoreshaAfyaConfigs.familyBa))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    @Order(order = 3)
    public void checkScanQr() throws InterruptedException{
        onView(withId(R.id.action_scan_qr))
                .check(matches(isDisplayed()));
    }



    public void confirmQrScanFunctionality() throws Throwable {
        onView(withId(R.id.action_scan_qr))
                .perform(click());
        Activity activity = getCurrentActivity();
        Assert.assertEquals("org.smartregister.view.activity.BarcodeScanActivity<org." +
                "smartregister.view.activity.BarcodeScanActivity@85dfc8d>", activity);
        //onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Scan QR Code"))
                //.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        //cmp=org.smartregister.chw.ba/org.smartregister.view.activity.BarcodeScanActivity
    }

    @After
    public void completeTests(){
            mActivityTestRule.finishActivity();
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
}
