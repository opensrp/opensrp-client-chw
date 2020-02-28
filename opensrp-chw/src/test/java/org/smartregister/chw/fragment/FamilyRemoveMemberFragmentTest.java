package org.smartregister.chw.fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.presenter.FamilyRemoveMemberPresenter;
import org.smartregister.chw.shadows.FamilyRemoveMemberConfirmDialogShadow;
import org.smartregister.commonregistry.CommonPersonObjectClient;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22, shadows = {FamilyRemoveMemberConfirmDialogShadow.class})
public class FamilyRemoveMemberFragmentTest {

    @Test
    public void verifyRemoveMemberOnFragmentIsCalled() {
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = Mockito.mock(FamilyRemoveMemberPresenter.class);
        FamilyRemoveMemberFragment familyRemoveMemberFragment = Mockito.spy(FamilyRemoveMemberFragment.class);
        ReflectionHelpers.setField(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        // verify that remove object is called
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        familyRemoveMemberFragment.removeMember(client);
        Mockito.verify(familyRemoveMemberPresenter).removeMember(client);
    }

    @Test
    public void testCloseFamily() {
        // close family will send a close family command to the presenter
        FamilyRemoveMemberFragment familyRemoveMemberFragment = Mockito.spy(FamilyRemoveMemberFragment.class);
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = Mockito.mock(FamilyRemoveMemberPresenter.class);
        ReflectionHelpers.setField(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        String familyName = "";
        String details = "";
        familyRemoveMemberFragment.closeFamily(familyName, details);
        Mockito.verify(familyRemoveMemberPresenter).removeEveryone(familyName, details);
    }


    @Test
    public void testRemoveMember() {
        FamilyRemoveMemberFragment familyRemoveMemberFragment = Mockito.spy(FamilyRemoveMemberFragment.class);
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = Mockito.mock(FamilyRemoveMemberPresenter.class);
        ReflectionHelpers.setField(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        familyRemoveMemberFragment.removeMember(client);
        Mockito.verify(familyRemoveMemberPresenter).removeMember(client);
    }
}
