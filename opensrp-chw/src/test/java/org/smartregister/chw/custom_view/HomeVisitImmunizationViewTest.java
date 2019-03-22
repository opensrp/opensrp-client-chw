package org.smartregister.chw.custom_view;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;



public class HomeVisitImmunizationViewTest  {
    @Mock
    HomeVisitImmunizationContract.Presenter presenter;

    @Mock
    HomeVisitImmunizationView homeVisitImmunizationView;


    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }

    @Test
    public void setChildClient() {
    }

    @Test
    public void refreshPresenter() {
    }

    @Test
    public void isGroupDoneThisVisit_false_on_emptyData() {
        PowerMockito.when(presenter.getVaccinesGivenThisVisit()).thenReturn(new ArrayList<VaccineWrapper>());
        PowerMockito.when(presenter.getNotGivenVaccines()).thenReturn(new ArrayList<VaccineWrapper>());
        PowerMockito.when(presenter.getVaccinesDueFromLastVisit()).thenReturn(new ArrayList<VaccineRepo.Vaccine>());
        Assert.assertFalse(homeVisitImmunizationView.isGroupDoneThisVisit(new HomeVisitVaccineGroupDetails()));


    }

    @Test
    public void initializePresenter() {
    }

    @Test
    public void getPresenter() {
    }
}