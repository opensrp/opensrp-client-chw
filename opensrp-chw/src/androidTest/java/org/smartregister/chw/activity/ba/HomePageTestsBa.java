package org.smartregister.chw.activity.ba;

import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

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

    private Utils utils = new Utils();

    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);



    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigUtils.ba_username, Constants.BoreshaAfyaConfigUtils.ba_password);
    }
    @Test
    @Order(order = 1)
    public void searchByName() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Configs.TestConfigHelper.familyName), closeSoftKeyboard());
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .check(matches(isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    @Order(order = 2)
    public void searchByID() throws InterruptedException{
        onView(withHint("Search name or ID"))
                .perform(typeText(Constants.BoreshaAfyaConfigUtils.searchFamilyIDBa), closeSoftKeyboard());
        onView(withSubstring(Constants.BoreshaAfyaConfigUtils.familyBa))
                .check(matches(isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    @Order(order = 3)
    public void checkScanQr() {
        onView(withId(R.id.action_scan_qr))
                .check(matches(isDisplayed()));
    }

    public void confirmQrScanFunctionality()  {
        onView(withId(R.id.action_scan_qr))
                .perform(click());
        //Assert.assertEquals("org.smartregister.view.activity.BarcodeScanActivity<org." +
                //"smartregister.view.activity.BarcodeScanActivity@85dfc8d>", activity);
    }

    @After
    public void completeTests(){
            mActivityTestRule.finishActivity();
        }
}
