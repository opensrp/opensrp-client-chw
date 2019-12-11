package org.smartregister.chw.interactor;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.domain.PncBaby;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultPncHomeVisitInteractorFlvTest extends BaseHomeVisitInteractorFlvTest {

    private DefaultPncHomeVisitInteractorFlv interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(DefaultPncHomeVisitInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

        Whitebox.setInternalState(interactor, "editMode", false);
        Whitebox.setInternalState(interactor, "context", context);
        Whitebox.setInternalState(interactor, "memberObject", memberObject);
        Whitebox.setInternalState(interactor, "actionList", actionList);

        CoreConstants.JSON_FORM.setLocaleAndAssetManager(locale, assetManager);
        Mockito.doReturn(title).when(context).getString(Mockito.anyInt());

        Mockito.doReturn(builder).when(builder).withOptional(Mockito.anyBoolean());
        Mockito.doReturn(builder).when(builder).withBaseEntityID(Mockito.any());
        Mockito.doReturn(builder).when(builder).withDetails(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withDestinationFragment(Mockito.any());
        Mockito.doReturn(builder).when(builder).withHelper(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withDisabledMessage(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withValidator(Mockito.any());
        Mockito.doReturn(builder).when(builder).withProcessingMode(Mockito.any());
        Mockito.doReturn(builder).when(builder).withFormName(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withJsonPayload(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withPayloadType(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withPayloadDetails(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withScheduleStatus(Mockito.any());
        //Mockito.doReturn(builder).when(builder).withSubtitle(Mockito.any());

        Mockito.doReturn(builder).when(interactor).getBuilder(Mockito.any());

        Mockito.doReturn(ancHomeVisitAction).when(builder).build();
        //Mockito.doReturn(visitRepository).when(interactor).getVisitRepository();
/*
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.doReturn("").when(jsonObject).toString();
        Mockito.doReturn(jsonObject).when(interactor).getFormJson(Mockito.anyString());
        Mockito.doReturn(jsonObject).when(interactor).getFormJson(Mockito.any(), Mockito.any());

 */
    }

    @Test
    public void testEvaluateDangerSignsMother() {

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDangerSignsMother");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateDangerSignsBaby() {

        List<PncBaby> children = new ArrayList<>();
        // String baseEntityID, String firstName, String lastName, String middleName, Date dob, String lbw
        children.add(new PncBaby("12345","Fname 1","Lname 1","Mname 1", new Date(), "yes"));
        children.add(new PncBaby("12345","Fname 2","Lname 2","Mname 2", new Date(), "yes"));


        Whitebox.setInternalState(interactor, "children", children);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDangerSignsBaby");

        Mockito.verify(actionList, Mockito.times(2)).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }
}
