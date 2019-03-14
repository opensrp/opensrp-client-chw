package org.smartgresiter.wcaro.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.domain.FamilyMember;
import org.smartgresiter.wcaro.interactor.FamilyChangeContractInteractor;
import org.smartgresiter.wcaro.model.FamilyChangeContractModel;

import java.util.ArrayList;
import java.util.List;

public class FamilyChangePresenterTest {

    FamilyChangePresenter presenter;

    @Mock
    FamilyChangeContractInteractor interactor;

    @Mock
    FamilyChangeContractModel model;

    @Mock
    FamilyChangeContract.View view;

    String familyID = "TEST_FAM_ID";

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
