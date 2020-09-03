package org.smartregister.chw.activity.wcaro;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                LoginPageActivityTest.class,
                HomePageTests.class,
                SideNavigationMenuTests.class,
                AddFamilyTestWcaro.class,
                AddFamilyFailTests.class,
                AddFamilyMemberTest.class,
                WashCheckVisitTest.class,
                AddChildFamilyMemberTest.class,
                AdditionalTestData.class,
                CallWidgetTests.class,
                EditTests.class,
                ANCRegistrationTests.class,
                ANCRegisterTests.class,
                ANCVisitTests.class,
                RemoveMemberTests.class,
                FamilyMemberTest.class
})
public class TestRun {

}
