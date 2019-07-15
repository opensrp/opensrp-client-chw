package org.smartregister.chw.interactor;

import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.TestChwApplication;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.Utils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, constants = BuildConfig.class, sdk = 22)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({ImmunizationLibrary.class, Utils.class})
public class DefaultAncHomeVisitInteractorFlvTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private BaseAncHomeVisitContract.InteractorCallBack callBack;

    @Mock
    private BaseAncHomeVisitContract.View view;

    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private RecurringServiceRecordRepository recurringServiceRecordRepository;

    /**
     * Check that this file actually compiles for the flavors
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateActions() throws Exception {
        PowerMockito.mockStatic(ImmunizationLibrary.class);
        PowerMockito.mockStatic(Utils.class);
        when(Utils.getLocalForm(ArgumentMatchers.anyString())).thenReturn("anc_hv_anc_iptp_sp");

        when(immunizationLibrary.recurringServiceRecordRepository()).thenReturn(recurringServiceRecordRepository);
        when(ImmunizationLibrary.getInstance()).thenReturn(immunizationLibrary);

        DefaultAncHomeVisitInteractorFlv flv = Mockito.mock(DefaultAncHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn("").when(context).getString(ArgumentMatchers.anyInt());
        Mockito.doReturn(context).when(view).getContext();

        VaccineTaskModel vaccineTaskModel = Mockito.mock(VaccineTaskModel.class);

//        Mockito.doReturn(vaccineTaskModel).when(flv).getWomanVaccine(ArgumentMatchers.anyString(), ArgumentMatchers.any(DateTime.class), ArgumentMatchers.<VaccineWrapper>anyList());

        MemberObject memberObject = new MemberObject();
        Whitebox.setInternalState(memberObject, "lastMenstrualPeriod", "01-01-2019");
        Whitebox.setInternalState(memberObject, "baseEntityId", "12345");

//        LinkedHashMap<String, BaseAncHomeVisitAction> actions = flv.calculateActions(view, memberObject, callBack);
//        assertTrue(actions.size() > 0);
    }

}
