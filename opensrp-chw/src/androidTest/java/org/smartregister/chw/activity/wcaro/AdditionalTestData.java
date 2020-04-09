package org.smartregister.chw.activity.wcaro;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.utils.Configs;
import org.smartregister.chw.activity.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

public class AdditionalTestData {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void test() {
        onView(ViewMatchers.withSubstring(Configs.TestConfigs.familyName + " Family"))
                .perform(click());
    }

    @After
    public void completeTests() throws Throwable{
        utils.addTestFamilyMember(Configs.AdditionalTestData.memberOneFirstname,
                Configs.AdditionalTestData.memberOneSecondname,
                Configs.AdditionalTestData.extraMemberAge1);
        utils.addTestFamilyMember(Configs.AdditionalTestData.memberTwoFirstname,
                Configs.AdditionalTestData.memberTwoSecondname,
                Configs.AdditionalTestData.extraMemberAge2);
        utils.addTestFamilyMember(Configs.AdditionalTestData.memberThreeFirstname,
                Configs.AdditionalTestData.memberThreeSecondname,
                Configs.AdditionalTestData.extraMemberAge3);
        mActivityTestRule.finishActivity();
    }

}
