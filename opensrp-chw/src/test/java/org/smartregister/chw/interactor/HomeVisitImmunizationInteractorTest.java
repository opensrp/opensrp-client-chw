package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.powermock.api.mockito.PowerMockito.doReturn;

public class HomeVisitImmunizationInteractorTest extends BaseUnitTest {
    private HomeVisitImmunizationInteractor interactor;
    @Mock
    HomeVisitImmunizationContract.InteractorCallBack callBack;
    @Mock
    AlertService alertService;
    @Mock
    VaccineRepository vaccineRepository;
    @Mock
    Observable<VaccineTaskModel> responseObserver;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.spy(HomeVisitImmunizationInteractor.class);
        Whitebox.setInternalState(interactor, "alertService", alertService);
        Whitebox.setInternalState(interactor, "vaccineRepository", vaccineRepository);
    }

    @Test
    public void updateImmunizationStateTest(){
       Map<String,String> details = new LinkedHashMap<>();
       details.put(DBConstants.KEY.DOB,"2018-08-17T06:00:00.000+06:00");
       CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("sds",details,"name");
       doReturn(responseObserver).when(interactor).getVaccineTask(commonPersonObjectClient,new ArrayList<VaccineWrapper>());
        TestObserver<VaccineTaskModel> testObserver = interactor.getVaccineTask(commonPersonObjectClient,new ArrayList<VaccineWrapper>()).test();
        testObserver.dispose();
        //interactor.updateImmunizationState(commonPersonObjectClient,new ArrayList<VaccineWrapper>(),callBack);
    }
}
