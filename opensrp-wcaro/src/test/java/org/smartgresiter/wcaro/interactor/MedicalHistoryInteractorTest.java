package org.smartgresiter.wcaro.interactor;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.presenter.MedicalHistoryPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;

public class MedicalHistoryInteractorTest extends BaseUnitTest {

    MedicalHistoryInteractor interactor;
    @Mock
    MedicalHistoryPresenter presenter;
    @Mock
    Context context;
    @Mock
    MedicalHistoryContract.InteractorCallBack callBack;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.spy(MedicalHistoryInteractor.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fetchFullyImmunizationData() {
    }

    @Test
    public void fetchBirthAndIllnessData_true_birthdata() {

        AppExecutors appExecutors = Mockito.spy(AppExecutors.class);
        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
        String caseId="cd6f66c8-3587-4c4d-b26d-f8753ba9dfa4";
        String name="";
        Map<String,String> mapBirth=new HashMap<>();
        mapBirth.put(BIRTH_CERT,"yes");
        mapBirth.put(BIRTH_CERT_NOTIFIICATION,"no");
        CommonPersonObjectClient commonPersonObjectClient=new CommonPersonObjectClient(caseId,mapBirth,name);
        commonPersonObjectClient.setColumnmaps(mapBirth);
        interactor.fetchBirthAndIllnessData(commonPersonObjectClient,callBack);
        verify(callBack,never()).updateBirthCertification(Mockito.any(ArrayList.class));

    }
    @Test
    public void fetchBirthAndIllnessData_true_illnessdata() {

        AppExecutors appExecutors = Mockito.spy(AppExecutors.class);
        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
        String caseId="cd6f66c8-3587-4c4d-b26d-f8753ba9dfa4";
        String name="";
        Map<String,String> mapIllness=new HashMap<>();
        mapIllness.put(ILLNESS_DATE,"04-02-2019");
        mapIllness.put(ILLNESS_DESCRIPTION,"description");
        mapIllness.put(ILLNESS_ACTION,"managed");
        CommonPersonObjectClient commonPersonObjectClient=new CommonPersonObjectClient(caseId,mapIllness,name);
        commonPersonObjectClient.setColumnmaps(mapIllness);
        android.content.Context ctx = Mockito.mock(android.content.Context.class);
        PowerMockito.when(interactor.getContext()).thenReturn(ctx);
        interactor.fetchBirthAndIllnessData(commonPersonObjectClient,callBack);
        verify(callBack,never()).updateBirthCertification(Mockito.any(ArrayList.class));

    }
    @Test
    public void setInitialVaccineList() {
    }

    @Test
    public void fetchGrowthNutritionData() {
    }
}