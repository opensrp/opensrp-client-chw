package org.smartregister.chw.interactor;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;

public class ChildHomeVisitInteractorFlvTest extends BaseHomeVisitInteractorFlvTest {

    private DefaultChildHomeVisitInteractorFlv interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(DefaultChildHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(interactor, "editMode", false);
        ReflectionHelpers.setField(interactor, "context", context);
        ReflectionHelpers.setField(interactor, "memberObject", memberObject);
        ReflectionHelpers.setField(interactor, "actionList", actionList);
        ReflectionHelpers.setField(interactor, "vaccineCardReceived", false);

        Date dob = LocalDate.now().minusDays(62).toDate();
        ReflectionHelpers.setField(interactor, "dob", dob);

        CoreConstants.JSON_FORM.setLocaleAndAssetManager(locale, assetManager);
        Mockito.doReturn(title).when(context).getString(Mockito.anyInt());

        Mockito.doReturn(builder).when(builder).withOptional(Mockito.anyBoolean());
        Mockito.doReturn(builder).when(builder).withBaseEntityID(Mockito.any());
        Mockito.doReturn(builder).when(builder).withDetails(Mockito.any());
        Mockito.doReturn(builder).when(builder).withDestinationFragment(Mockito.any());
        Mockito.doReturn(builder).when(builder).withHelper(Mockito.any());
        Mockito.doReturn(builder).when(builder).withProcessingMode(Mockito.any());
        Mockito.doReturn(builder).when(builder).withFormName(Mockito.any());
        Mockito.doReturn(builder).when(builder).withJsonPayload(Mockito.any());
        Mockito.doReturn(builder).when(builder).withPayloadType(Mockito.any());
        Mockito.doReturn(builder).when(builder).withPayloadDetails(Mockito.any());
        Mockito.doReturn(builder).when(builder).withScheduleStatus(Mockito.any());
        Mockito.doReturn(builder).when(builder).withSubtitle(Mockito.any());

        Mockito.doReturn(builder).when(interactor).getBuilder(Mockito.any());

        Mockito.doReturn(ancHomeVisitAction).when(builder).build();
        Mockito.doReturn(visitRepository).when(interactor).getVisitRepository();

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.doReturn("").when(jsonObject).toString();
        Mockito.doReturn(jsonObject).when(interactor).getFormJson(Mockito.anyString());
        Mockito.doReturn(jsonObject).when(interactor).getFormJson(Mockito.any(), Mockito.any());
    }

    @Test
    public void testEvaluateDietary() {
        Date dob = LocalDate.now().minusDays(280).toDate();
        ReflectionHelpers.setField(interactor, "dob", dob);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDietary");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }
}
