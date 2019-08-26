package org.smartregister.chw.interactor;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.application.TestChwApplication;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.chw.util.Utils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;

import java.util.LinkedHashMap;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, sdk = 22)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({ImmunizationLibrary.class, Utils.class, JsonFormUtils.class})
public class DefaultCoreAncHomeVisitInteractorFlvTest {

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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(JsonFormUtils.class);

        BDDMockito.given(JsonFormUtils.getFormAsJson(ArgumentMatchers.anyString())).willReturn(Mockito.mock(JSONObject.class));
    }

    @Test
    public void testCalculateActions() throws Exception {
        PowerMockito.mockStatic(ImmunizationLibrary.class);

        PowerMockito.when(immunizationLibrary.recurringServiceRecordRepository()).thenReturn(recurringServiceRecordRepository);
        PowerMockito.when(ImmunizationLibrary.getInstance()).thenReturn(immunizationLibrary);

        DefaultAncHomeVisitInteractorFlv flv = Mockito.mock(DefaultAncHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        Mockito.doReturn(RuntimeEnvironment.application).when(view).getContext();

        VaccineTaskModel vaccineTaskModel = Mockito.mock(VaccineTaskModel.class);

        Mockito.doReturn(vaccineTaskModel).when(flv).getWomanVaccine(ArgumentMatchers.anyString(), ArgumentMatchers.any(DateTime.class), ArgumentMatchers.anyList());

        MemberObject memberObject = new MemberObject();
        Whitebox.setInternalState(memberObject, "lastMenstrualPeriod", "01-01-2019");
        Whitebox.setInternalState(memberObject, "baseEntityId", "12345");

        LinkedHashMap<String, BaseAncHomeVisitAction> actions = flv.calculateActions(view, memberObject, callBack);
        TestCase.assertTrue(actions.size() > 0);
    }

}
