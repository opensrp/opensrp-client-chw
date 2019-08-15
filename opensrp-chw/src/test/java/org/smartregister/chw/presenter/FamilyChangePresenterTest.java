package org.smartregister.chw.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;
import org.smartregister.chw.core.model.FamilyChangeContractModel;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;

import java.util.ArrayList;
import java.util.List;

public class FamilyChangePresenterTest {

    private CoreFamilyChangePresenter presenter;

    @Mock
    private CoreFamilyChangeContractInteractor interactor;

    @Mock
    private FamilyChangeContractModel model;

    @Mock
    private FamilyChangeContract.View view;

    private String familyID = "TEST_FAM_ID";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = new FamilyChangePresenter(view, familyID);
        Whitebox.setInternalState(presenter, "interactor", interactor);
        Whitebox.setInternalState(presenter, "model", model);
    }

    @Test
    public void testSaveCompleted() {
        presenter.saveCompleted(familyID, null);
        Mockito.verify(view).saveComplete(familyID, null);
    }


    @Test
    public void testGetAdultMembersExcludePCG() {
        presenter.getAdultMembersExcludeHOF();
        Mockito.verify(interactor).getAdultMembersExcludeHOF(familyID, presenter);
    }


    @Test
    public void testRenderAdultMembersExcludePCG() {
        List<FamilyMember> clients = new ArrayList<>();
        String primaryCareID = "";
        String headOfHouseID = familyID;

        presenter.renderAdultMembersExcludePCG(clients, primaryCareID, headOfHouseID);
        Mockito.verify(model).getMembersExcluding(clients, primaryCareID, headOfHouseID, primaryCareID);
    }

    @Test
    public void testGetAdultMembersExcludeHOF() {

        presenter.getAdultMembersExcludeHOF();
        Mockito.verify(interactor).getAdultMembersExcludeHOF(familyID, presenter);
    }

    @Test
    public void testRenderAdultMembersExcludeHOF() {
        List<FamilyMember> clients = new ArrayList<>();
        String primaryCareID = "";
        String headOfHouseID = familyID;

        presenter.renderAdultMembersExcludeHOF(clients, primaryCareID, headOfHouseID);
        model.getMembersExcluding(clients, primaryCareID, headOfHouseID, headOfHouseID);
    }
}
