package org.smartgresiter.wcaro.interactor;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartgresiter.wcaro.presenter.MedicalHistoryPresenter;
import org.smartregister.family.util.AppExecutors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class MedicalHistoryInteractorTest {

    MedicalHistoryInteractor interactor;
    MedicalHistoryPresenter presenter;
    Context context;

    @Before
    public void setUp() throws Exception {
        interactor = Mockito.spy(MedicalHistoryInteractor.class);
        presenter = Mockito.mock(MedicalHistoryPresenter.class);
        context = Mockito.mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fetchFullyImmunizationData() {
    }

    @Test
    public void fetchBirthAndIllnessData() {

        AppExecutors appExecutors = Mockito.spy(AppExecutors.class);

        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);

    }

    @Test
    public void setInitialVaccineList() {
    }

    @Test
    public void fetchGrowthNutritionData() {
    }
}