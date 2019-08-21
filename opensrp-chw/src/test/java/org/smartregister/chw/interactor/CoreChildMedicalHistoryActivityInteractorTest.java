package org.smartregister.chw.interactor;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;

public class CoreChildMedicalHistoryActivityInteractorTest extends BaseUnitTest {

    private ChildMedicalHistoryInteractor interactor;
    private AppExecutors appExecutors;
    @Mock
    private ChildMedicalHistoryContract.InteractorCallBack callBack;
    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appExecutors = Mockito.spy(AppExecutors.class);
        interactor = Mockito.spy(ChildMedicalHistoryInteractor.class);
        context = ChwApplication.getInstance().getApplicationContext();

    }

    @Test
    public void fetchBirthAndIllnessDataTrueBirthdata() {

        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
        String caseId = "cd6f66c8-3587-4c4d-b26d-f8753ba9dfa4";
        String name = "";
        Map<String, String> mapBirth = new HashMap<>();
        mapBirth.put(BIRTH_CERT, "yes");
        mapBirth.put(BIRTH_CERT_NOTIFIICATION, "no");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(caseId, mapBirth, name);
        commonPersonObjectClient.setColumnmaps(mapBirth);
        PowerMockito.when(interactor.getContext()).thenReturn(context);
        interactor.fetchBirthCertificateData(commonPersonObjectClient, callBack);
        verify(callBack, never()).updateBirthCertification(Mockito.any(ArrayList.class));

    }

    @Test
    public void fetchBirthAndIllnessDataTrueIllnessdata() {

        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
        String caseId = "cd6f66c8-3587-4c4d-b26d-f8753ba9dfa4";
        String name = "";
        Map<String, String> mapIllness = new HashMap<>();
        mapIllness.put(ILLNESS_DATE, "04-02-2019");
        mapIllness.put(ILLNESS_DESCRIPTION, "description");
        mapIllness.put(ILLNESS_ACTION, "managed");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(caseId, mapIllness, name);
        commonPersonObjectClient.setColumnmaps(mapIllness);
        PowerMockito.when(interactor.getContext()).thenReturn(context);
        interactor.fetchBirthCertificateData(commonPersonObjectClient, callBack);
        verify(callBack, never()).updateBirthCertification(Mockito.any(ArrayList.class));

    }

}