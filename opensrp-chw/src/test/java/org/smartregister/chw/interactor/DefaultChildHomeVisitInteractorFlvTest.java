package org.smartregister.chw.interactor;

import android.content.Context;
import android.content.res.AssetManager;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DefaultChildHomeVisitInteractorFlvTest {

    @Mock
    private MemberObject memberObject;

    @Mock
    private LinkedHashMap<String, BaseAncHomeVisitAction> actionList;

    @Mock
    private Context context;

    private Locale locale = Locale.ENGLISH;

    @Mock
    private AssetManager assetManager;

    private DefaultChildHomeVisitInteractorFlv interactor;

    private final String title = "Sample Title";

    @Mock
    private BaseAncHomeVisitAction.Builder builder;

    @Mock
    private BaseAncHomeVisitAction ancHomeVisitAction;

    /**
     * Check that this file actually compiles for the flavors
     */
    @Before
    public void setUp() throws BaseAncHomeVisitAction.ValidationException {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(DefaultChildHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        Whitebox.setInternalState(interactor, "editMode", false);
        Whitebox.setInternalState(interactor, "context", context);
        Whitebox.setInternalState(interactor, "memberObject", memberObject);
        Whitebox.setInternalState(interactor, "actionList", actionList);
        Whitebox.setInternalState(interactor, "vaccineCardReceived", false);

        Date dob = LocalDate.now().minusDays(61).toDate();
        Whitebox.setInternalState(interactor, "dob", dob);

        CoreConstants.JSON_FORM.setLocaleAndAssetManager(locale, assetManager);
        Mockito.doReturn(title).when(context).getString(Mockito.anyInt());

        Mockito.doReturn(builder).when(builder).withOptional(Mockito.anyBoolean());
        Mockito.doReturn(builder).when(builder).withBaseEntityID(Mockito.any());
        Mockito.doReturn(builder).when(builder).withDetails(Mockito.any());
        Mockito.doReturn(builder).when(builder).withDestinationFragment(Mockito.any());
        Mockito.doReturn(builder).when(builder).withHelper(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withDisabledMessage(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withValidator(Mockito.any());
        Mockito.doReturn(builder).when(builder).withProcessingMode(Mockito.any());
        Mockito.doReturn(builder).when(builder).withFormName(Mockito.any());
        Mockito.doReturn(builder).when(builder).withJsonPayload(Mockito.any());

        Mockito.doReturn(ancHomeVisitAction).when(builder).build();
    }

    @Test
    public void testEvaluateChildVaccineCard() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateChildVaccineCard"
                , ReflectionHelpers.ClassParameter.from(BaseAncHomeVisitAction.Builder.class, builder));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateDietary() {
        Date dob = LocalDate.now().minusDays(280).toDate();
        Whitebox.setInternalState(interactor, "dob", dob);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDietary"
                , ReflectionHelpers.ClassParameter.from(BaseAncHomeVisitAction.Builder.class, builder));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateECD() throws Exception {
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.doReturn("").when(jsonObject).toString();
        Mockito.doReturn(jsonObject).when(interactor).getFormJson(Mockito.anyString());

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateECD"
                , ReflectionHelpers.ClassParameter.from(BaseAncHomeVisitAction.Builder.class, builder));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateLLITN() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateLLITN"
                , ReflectionHelpers.ClassParameter.from(BaseAncHomeVisitAction.Builder.class, builder));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateObsAndIllness() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateBirthCertForm"
                , ReflectionHelpers.ClassParameter.from(BaseAncHomeVisitAction.Builder.class, builder));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testGetAgeInMonths() {
        int months = ReflectionHelpers.callInstanceMethod(interactor, "getAgeInMonths");
        assertEquals(2, months);
    }
}
