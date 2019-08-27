package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.cloudant.models.Client;
import org.smartregister.cloudant.models.Event;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyOtherMemberProfileInteractor;

/**
 * Created by keyman on 11/03/2019.
 */
public class FamilyOtherMemberActivityPresenterTest extends BaseUnitTest {

    @Mock
    private FamilyOtherMemberProfileExtendedContract.View view;

    @Mock
    private FamilyOtherMemberContract.Model model;

    @Mock
    private FamilyProfileContract.Interactor profileInteractor;

    @Mock
    private FamilyProfileContract.Model profileModel;

    @Mock
    private FamilyOtherMemberProfileInteractor interactor;

    private FamilyOtherMemberContract.Presenter presenter;

    private String viewConfigurationIdentifier;
    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        viewConfigurationIdentifier = "viewConfigurationIdentifier";
        familyBaseEntityId = "familyBaseEntityId";
        baseEntityId = "baseEntityId";
        familyHead = "familyHead";
        primaryCaregiver = "primaryCaregiver";
        villageTown = "villageTown";
        familyName = "familyName";

        presenter = new FamilyOtherMemberActivityPresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Test
    public void testFields() {
        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;
        Assert.assertEquals("familyBaseEntityId", familyOtherMemberActivityPresenter.getFamilyBaseEntityId());
        Assert.assertEquals("familyName", familyOtherMemberActivityPresenter.getFamilyName());
    }

    @Test
    public void testUpdateFamilyMember() {
        final String jsonString = "Json_File";
        final FamilyEventClient familyEventClient = new FamilyEventClient(new Client(), new Event());

        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;

        FamilyOtherMemberActivityPresenter spyPresenter = Mockito.spy(familyOtherMemberActivityPresenter);
        Whitebox.setInternalState(spyPresenter, "profileInteractor", profileInteractor);
        Whitebox.setInternalState(spyPresenter, "profileModel", profileModel);

        Mockito.doReturn(familyEventClient).when(profileModel).processUpdateMemberRegistration(jsonString, familyBaseEntityId);

        spyPresenter.updateFamilyMember(jsonString);

        Mockito.verify(view).showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);
        Mockito.verify(profileModel).processUpdateMemberRegistration(jsonString, familyBaseEntityId);
        Mockito.verify(profileInteractor).saveRegistration(familyEventClient, jsonString, true, spyPresenter);

    }

    @Test
    public void testUpdateFamilyMemberThrowsException() {
        final String jsonString = "Json_File";
        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;

        FamilyOtherMemberActivityPresenter spyPresenter = Mockito.spy(familyOtherMemberActivityPresenter);
        Whitebox.setInternalState(spyPresenter, "profileInteractor", profileInteractor);
        Whitebox.setInternalState(spyPresenter, "profileModel", profileModel);

        Mockito.doThrow(new RuntimeException()).when(profileModel).processUpdateMemberRegistration(jsonString, familyBaseEntityId);

        spyPresenter.updateFamilyMember(jsonString);

        Mockito.verify(view).showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);
        Mockito.verify(view).hideProgressDialog();

    }

    @Test
    public void testOnRegistrationSaved() {
        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;

        FamilyOtherMemberActivityPresenter spyPresenter = Mockito.spy(familyOtherMemberActivityPresenter);
        Whitebox.setInternalState(spyPresenter, "interactor", interactor);

        spyPresenter.onRegistrationSaved(true);

        Mockito.verify(view).hideProgressDialog();
        Mockito.verify(interactor).refreshProfileView(baseEntityId, spyPresenter);
        Mockito.verify(view).refreshList();

    }

    @Test
    public void testOnRegistrationSavedNotEditMode() {
        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;

        FamilyOtherMemberActivityPresenter spyPresenter = Mockito.spy(familyOtherMemberActivityPresenter);
        Whitebox.setInternalState(spyPresenter, "interactor", interactor);

        spyPresenter.onRegistrationSaved(false);

        Mockito.verify(view, Mockito.times(0)).hideProgressDialog();
        Mockito.verify(interactor, Mockito.times(0)).refreshProfileView(baseEntityId, spyPresenter);
        Mockito.verify(view, Mockito.times(0)).refreshList();

    }

}
