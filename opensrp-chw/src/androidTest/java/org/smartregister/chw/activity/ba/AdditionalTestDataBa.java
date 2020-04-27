package org.smartregister.chw.activity.ba;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Constants;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

public class AdditionalTestDataBa {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    public void setUp() throws InterruptedException {
        utils.logIn(Constants.BoreshaAfyaConfigUtils.ba_username, Constants.BoreshaAfyaConfigUtils.ba_password);
    }


    @Test
    public void test() {
        onView(ViewMatchers.withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
    }

    @After
    public void completeTests() throws Throwable{
        utils.addTestFamilyMemberBa(Configs.AdditionalTestDataHelper.memberOneFirstname,
                Configs.AdditionalTestDataHelper.memberOneSecondname,
                Configs.AdditionalTestDataHelper.extraMemberAge1);
        utils.addTestFamilyMemberBa(Configs.AdditionalTestDataHelper.memberTwoFirstname,
                Configs.AdditionalTestDataHelper.memberTwoSecondname,
                Configs.AdditionalTestDataHelper.extraMemberAge2);
        mActivityTestRule.finishActivity();
    }

}
