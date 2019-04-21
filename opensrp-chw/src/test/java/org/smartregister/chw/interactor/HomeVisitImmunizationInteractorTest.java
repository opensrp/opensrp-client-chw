package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChwApplication.class})
public class HomeVisitImmunizationInteractorTest extends BaseUnitTest {
    @Mock
    private HomeVisitImmunizationInteractor interactor;
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    HomeVisitImmunizationContract.InteractorCallBack callBack;
    private String dobString = "";
    private String entityId = "a4fa50a5-12ab-442b-b0e5-49b3ba905b8a";
    private TestScheduler scheduler;
    @Mock
    Observer<VaccineTaskModel> responseObserver;
    @Mock
    AlertService alertService;
    @Mock
    VaccineRepository vaccineRepository;
    @Mock
    ChwApplication chwApplication;
    @Mock
    private Context context;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ChwApplication.class);
        PowerMockito.when(ChwApplication.getInstance()).thenReturn(chwApplication);
        PowerMockito.when(ChwApplication.getInstance().getContext()).thenReturn(context);
        PowerMockito.when(ChwApplication.getInstance().getContext().alertService()).thenReturn(alertService);
        PowerMockito.when(ChwApplication.getInstance().vaccineRepository()).thenReturn(vaccineRepository);
        interactor = Mockito.spy(HomeVisitImmunizationInteractor.class);
        scheduler = new TestScheduler();

    }
    @Test
    public void updateImmunizationState_WhenDataAvailable_True_Callback() throws Exception{
        //TestSubject<VaccineTaskModel> eventObservable = TestSubject.create(scheduler);
        Whitebox.setInternalState(interactor, "processScheduler", scheduler);
        Whitebox.setInternalState(interactor, "androidScheduler", scheduler);
        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
        vaccineTaskModel.setNotGivenVaccine(new ArrayList<VaccineWrapper>());
        interactor.getVaccineTask(dobString, entityId,new ArrayList<VaccineWrapper>());
       // Whitebox.invokeMethod(interactor, "getVaccineTask", details, entityId,Mockito.any(ArrayList.class));
        scheduler.triggerActions();
        scheduler.advanceTimeBy(10,TimeUnit.SECONDS);
        verify(responseObserver, times(1)).onNext(vaccineTaskModel);
    }
}
