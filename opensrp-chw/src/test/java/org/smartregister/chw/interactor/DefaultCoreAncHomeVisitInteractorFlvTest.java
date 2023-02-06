package org.smartregister.chw.interactor;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.application.TestChwApplication;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.chw.shadows.ImmunizationLibraryShadow;
import org.smartregister.chw.shadows.JsonFormUtilsShadow;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;

import java.util.LinkedHashMap;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class,
        shadows = {JsonFormUtilsShadow.class, ImmunizationLibraryShadow.class})
public class DefaultCoreAncHomeVisitInteractorFlvTest {

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
    }

    @Test
    public void testCalculateActions() throws Exception {
        ImmunizationLibraryShadow.setImmunizationLibrary(immunizationLibrary);
        Mockito.doReturn(recurringServiceRecordRepository).when(immunizationLibrary).recurringServiceRecordRepository();

        DefaultAncHomeVisitInteractorFlv flv = Mockito.mock(DefaultAncHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        Mockito.doReturn(RuntimeEnvironment.application).when(view).getContext();

        VaccineTaskModel vaccineTaskModel = Mockito.mock(VaccineTaskModel.class);

        Mockito.doReturn(vaccineTaskModel).when(flv).getWomanVaccine(ArgumentMatchers.anyString(), ArgumentMatchers.any(DateTime.class), ArgumentMatchers.anyList());

        MemberObject memberObject = new MemberObject();
        ReflectionHelpers.setField(memberObject, "lastMenstrualPeriod", "01-01-2019");
        ReflectionHelpers.setField(memberObject, "baseEntityId", "12345");

        LinkedHashMap<String, BaseAncHomeVisitAction> actions = flv.calculateActions(view, memberObject, callBack);
        TestCase.assertTrue(actions.size() > 0);
    }

}
