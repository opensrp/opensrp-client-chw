package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.chw.interactor.ChildMedicalHistoryInteractor;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CoreChildMedicalHistoryActivityPresenterTest {


    private ChildMedicalHistoryPresenter presenter;

    @Mock
    private ChildMedicalHistoryContract.View view;

    @Mock
    private ChildMedicalHistoryInteractor interactor;

    @Mock
    private CommonPersonObjectClient commonPersonObjectClient;

    @Mock
    private VisitRepository homeVisitServiceRepository;

    @Mock
    private AppExecutors appExecutors;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ChildMedicalHistoryPresenter(view, appExecutors, homeVisitServiceRepository);
        Whitebox.setInternalState(presenter, "interactor", interactor);
    }

    @Test
    public void testSetInitialVaccineList() {

        Map<String, Date> vaccineList = new HashMap<>();

        presenter.setInitialVaccineList(vaccineList);
        Mockito.verify(interactor).setInitialVaccineList(vaccineList, presenter);
    }

    @Test
    public void testFetchGrowthNutrition() {
        presenter.fetchGrowthNutrition(commonPersonObjectClient);
        Mockito.verify(interactor).fetchGrowthNutritionData(commonPersonObjectClient, presenter);
    }

    @Test
    public void testFetchFullyImmunization() {

        String dateOfBirth = "1234567";

        presenter.fetchFullyImmunization(dateOfBirth);
        Mockito.verify(interactor).fetchFullyImmunizationData(dateOfBirth, null, presenter);
    }

    @Test
    public void testFetchBirthAndIllnessData() {

        CommonPersonObjectClient commonPersonObjectClient = Mockito.mock(CommonPersonObjectClient.class);

        presenter.fetchBirthData(commonPersonObjectClient);
        Mockito.verify(interactor).fetchBirthCertificateData(commonPersonObjectClient, presenter);
    }


    @Test
    public void testUpdateFullyImmunization() {

        String text = "asdasdsdf";

        presenter.updateFullyImmunization(text);
        Mockito.verify(view).updateFullyImmunization(text);
    }

    @Test
    public void testGetVaccineBaseItem() {
        ArrayList<BaseVaccine> baseVaccineArrayList = new ArrayList<>();
        Whitebox.setInternalState(presenter, "baseVaccineArrayList", baseVaccineArrayList);

        Assert.assertEquals(baseVaccineArrayList, presenter.getVaccineBaseItem());
    }

    @Test
    public void testGetGrowthNutrition() {
        ArrayList<BaseVaccine> growthNutritionArrayList = new ArrayList<>();
        Whitebox.setInternalState(presenter, "growthNutritionArrayList", growthNutritionArrayList);

        Assert.assertEquals(growthNutritionArrayList, presenter.getGrowthNutrition());
    }

    @Test
    public void testGetBirthCertification() {
        ArrayList<BaseVaccine> birthCertifications = new ArrayList<>();
        Whitebox.setInternalState(presenter, "birthCertifications", birthCertifications);

        Assert.assertEquals(birthCertifications, presenter.getBirthCertification());
    }

    @Test
    public void testGetObsIllness() {
        ArrayList<BaseVaccine> obsIllnesses = new ArrayList<>();
        Whitebox.setInternalState(presenter, "obsIllnesses", obsIllnesses);

        Assert.assertEquals(obsIllnesses, presenter.getObsIllness());
    }

    @Test
    public void testUpdateBirthCertification() {

        presenter.updateBirthCertification(null);
        Mockito.verify(view).updateBirthCertification();

    }

    @Test
    public void updateIllnessData() {
        presenter.updateIllnessData(null);
        Mockito.verify(view).updateObsIllness();
    }

    @Test
    public void updateVaccineData() {
        presenter.updateVaccineData(null);
        Mockito.verify(view).updateVaccinationData();
    }

    @Test
    public void updateGrowthNutrition() {
        presenter.updateGrowthNutrition(null);
        Mockito.verify(view).updateGrowthNutrition();
    }

}
