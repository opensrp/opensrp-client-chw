package org.smartregister.chw.activity.wcaro;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

public class AdditionalTestData {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void test() {
        onView(withSubstring(Configs.TestConfigHelper.familyName + " Family"))
                .perform(click());
    }

    @After
    public void completeTests() throws Throwable{
        utils.addTestFamilyMember(Configs.AdditionalTestDataHelper.memberOneFirstname,
                Configs.AdditionalTestDataHelper.memberOneSecondname,
                Configs.AdditionalTestDataHelper.extraMemberAge1);
        utils.addTestFamilyMember(Configs.AdditionalTestDataHelper.memberTwoFirstname,
                Configs.AdditionalTestDataHelper.memberTwoSecondname,
                Configs.AdditionalTestDataHelper.extraMemberAge2);
        utils.addTestFamilyMember(Configs.AdditionalTestDataHelper.memberThreeFirstname,
                Configs.AdditionalTestDataHelper.memberThreeSecondname,
                Configs.AdditionalTestDataHelper.extraMemberAge3);
        mActivityTestRule.finishActivity();
    }

}
