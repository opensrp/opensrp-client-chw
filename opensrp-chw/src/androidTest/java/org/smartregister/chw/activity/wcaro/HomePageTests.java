package org.smartregister.chw.activity.wcaro;

import android.Manifest;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(OrderedRunner.class)
public class HomePageTests {
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

    @Before
    public void setUp() throws InterruptedException {
        utils.logIn(Constants.WcaroConfigs.wCaro_username, Constants.WcaroConfigs.wCaro_password);
    }
    @Test
    public void searchByName() throws InterruptedException{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Constants.WcaroConfigs.searchFamilyWCaro), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.WcaroConfigs.familyWcaro))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(1000);
        utils.openDrawer();
        utils.logOut();
    }

    @Test
    public void searchByID() throws InterruptedException{
        onView(ViewMatchers.withHint("Search name or ID"))
                .perform(typeText(Constants.WcaroConfigs.searchFamilyIDWCaro), closeSoftKeyboard());
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring(Constants.WcaroConfigs.familyWcaro))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(1000);
        utils.openDrawer();
        utils.logOut();
    }

    @Test
    public void checkJobAids() throws InterruptedException{
        onView(withId(R.id.action_job_aids))
                .check(matches(isDisplayed()));
        utils.openDrawer();
        utils.logOut();
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
