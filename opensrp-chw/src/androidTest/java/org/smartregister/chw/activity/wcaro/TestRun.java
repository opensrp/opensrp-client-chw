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
                AddChildFamilyMemberTest.class,
                AdditionalTestData.class,
                EditTests.class,
                CallWidgetTests.class,
                ANCRegistrationTests.class,
                WashCheckVisitTest.class,
                ANCRegisterTests.class,
                RemoveMemberTests.class,
                FamilyMemberTest.class
})
public class TestRun {

}
