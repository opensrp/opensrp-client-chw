package org.smartregister.chw.fragment;

import android.content.Context;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.presenter.FamilyRemoveMemberPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, constants = BuildConfig.class, sdk = 22)
@PowerMockIgnore({"org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(FamilyRemoveMemberConfirmDialog.class)
public class FamilyRemoveMemberFragmentTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void verifyRemoveMemberOnFragmentIsCalled() {
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = mock(FamilyRemoveMemberPresenter.class);
        FamilyRemoveMemberFragment familyRemoveMemberFragment = spy(FamilyRemoveMemberFragment.class);
        Whitebox.setInternalState(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        // verify that remove object is called
        CommonPersonObjectClient client = mock(CommonPersonObjectClient.class);
        familyRemoveMemberFragment.removeMember(client);
        verify(familyRemoveMemberPresenter).removeMember(client);
    }

    @Test
    public void testConfirmRemoveDialog() {
        FamilyRemoveMemberFragment familyRemoveMemberFragment = spy(FamilyRemoveMemberFragment.class);
        FamilyRemoveMemberConfirmDialog dialog = mock(FamilyRemoveMemberConfirmDialog.class);
        //given
        PowerMockito.mockStatic(FamilyRemoveMemberConfirmDialog.class);
        BDDMockito.given(FamilyRemoveMemberConfirmDialog.newInstance(anyString())).willReturn(dialog);

        Context context = mock(Context.class);
        Mockito.when(context.getString(any(Integer.class))).thenReturn("Test String");

        Mockito.when(familyRemoveMemberFragment.getContext()).thenReturn(context);

        Whitebox.setInternalState(familyRemoveMemberFragment, "memberName", "Family Name");
        Whitebox.setInternalState(familyRemoveMemberFragment, "processingFamily", true);

        familyRemoveMemberFragment.confirmRemove(new JSONObject());
        verify(dialog).show(familyRemoveMemberFragment.getFragmentManager(),FamilyRemoveMemberFragment.DIALOG_TAG);

    }

    @Test
    public void testCloseFamily() {
        // close family will send a close family command to the presenter
        FamilyRemoveMemberFragment familyRemoveMemberFragment = spy(FamilyRemoveMemberFragment.class);
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = mock(FamilyRemoveMemberPresenter.class);
        Whitebox.setInternalState(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        String familyName = "" , details = "";
        familyRemoveMemberFragment.closeFamily(familyName, details);
        verify(familyRemoveMemberPresenter).removeEveryone(familyName, details);
    }


    @Test
    public void testRemoveMember() {
        FamilyRemoveMemberFragment familyRemoveMemberFragment = spy(FamilyRemoveMemberFragment.class);
        FamilyRemoveMemberPresenter familyRemoveMemberPresenter = mock(FamilyRemoveMemberPresenter.class);
        Whitebox.setInternalState(familyRemoveMemberFragment, "presenter", familyRemoveMemberPresenter);

        CommonPersonObjectClient client = mock(CommonPersonObjectClient.class);
        familyRemoveMemberFragment.removeMember(client);
        verify(familyRemoveMemberPresenter).removeMember(client);
    }
}
