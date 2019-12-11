package org.smartregister.chw.interactor;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class DefaultChildHomeVisitInteractorFlvTest extends BaseHomeVisitInteractorFlvTest {

    private DefaultChildHomeVisitInteractorFlv interactor;

    @Before
    public void setUp() throws Exception {
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
    public void testCalculateActionsPopulatesServicesForChild() throws BaseAncHomeVisitAction.ValidationException {

        Mockito.doReturn(context).when(view).getContext();
        Mockito.doReturn(false).when(view).getEditMode();

        Date dob = LocalDate.now().minusDays(70).toDate();
        Mockito.doReturn(new SimpleDateFormat("yyyy-MM-dd").format(dob)).when(memberObject).getDob();

        Repository repository = Mockito.mock(Repository.class);
        ReflectionHelpers.setStaticField(AbstractDao.class, "repository", repository);

        Map<String, ServiceWrapper> serviceWrapperMap = new HashMap<>();
        Mockito.doReturn(serviceWrapperMap).when(interactor).getServices();

        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        LinkedHashMap<String, BaseAncHomeVisitAction> services = interactor.calculateActions(view, memberObject, callBack);

        Assert.assertTrue(services.size() > 0);
    }

    @Test
    public void testGetDetailsReturnsDBResultsEditing() {
        Whitebox.setInternalState(interactor, "editMode", true);

        String baseID = "12345";
        String eventName = "Sample Event Name";

        Mockito.doReturn(baseID).when(memberObject).getBaseEntityId();

        ReflectionHelpers.callInstanceMethod(interactor, "getDetails"
                , ReflectionHelpers.ClassParameter.from(String.class, eventName));

        Mockito.verify(visitRepository).getLatestVisit(baseID, eventName);
    }

    @Test
    public void testEvaluateChildVaccineCard() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateChildVaccineCard");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateExclusiveBreastFeeding() {
        Map<String, ServiceWrapper> serviceWrapperMap = new HashMap<>();
        ServiceWrapper serviceWrapper = Mockito.mock(ServiceWrapper.class);
        Mockito.doReturn("Exclusive 1").when(serviceWrapper).getName();
        Mockito.doReturn(new DateTime()).when(serviceWrapper).getVaccineDate();
        serviceWrapperMap.put("Exclusive breastfeeding", serviceWrapper);

        Alert alert = Mockito.mock(Alert.class);
        Mockito.doReturn("2019-01-01").when(alert).startDate();
        Mockito.doReturn(alert).when(serviceWrapper).getAlert();

        Mockito.doReturn("My name").when(context).getString(Mockito.anyInt(), Mockito.any());


        ReflectionHelpers.callInstanceMethod(interactor, "evaluateExclusiveBreastFeeding"
                , ReflectionHelpers.ClassParameter.from(Map.class, serviceWrapperMap));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateVitaminA() {
        Map<String, ServiceWrapper> serviceWrapperMap = new HashMap<>();
        ServiceWrapper serviceWrapper = Mockito.mock(ServiceWrapper.class);
        Mockito.doReturn("Vitamin A 1").when(serviceWrapper).getName();
        Mockito.doReturn(new DateTime()).when(serviceWrapper).getVaccineDate();
        serviceWrapperMap.put("Vitamin A", serviceWrapper);

        Alert alert = Mockito.mock(Alert.class);
        Mockito.doReturn("2019-01-01").when(alert).startDate();
        Mockito.doReturn(alert).when(serviceWrapper).getAlert();

        Mockito.doReturn("My name").when(context).getString(Mockito.anyInt(), Mockito.any());


        ReflectionHelpers.callInstanceMethod(interactor, "evaluateVitaminA"
                , ReflectionHelpers.ClassParameter.from(Map.class, serviceWrapperMap));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateDeworming() {
        Map<String, ServiceWrapper> serviceWrapperMap = new HashMap<>();
        ServiceWrapper serviceWrapper = Mockito.mock(ServiceWrapper.class);
        Mockito.doReturn("Deworming 1").when(serviceWrapper).getName();
        Mockito.doReturn(new DateTime()).when(serviceWrapper).getVaccineDate();
        serviceWrapperMap.put("Deworming", serviceWrapper);

        Alert alert = Mockito.mock(Alert.class);
        Mockito.doReturn("2019-01-01").when(alert).startDate();
        Mockito.doReturn(alert).when(serviceWrapper).getAlert();

        Mockito.doReturn("My name").when(context).getString(Mockito.anyInt(), Mockito.any());


        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDeworming"
                , ReflectionHelpers.ClassParameter.from(Map.class, serviceWrapperMap));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateMNP() {
        Map<String, ServiceWrapper> serviceWrapperMap = new HashMap<>();
        ServiceWrapper serviceWrapper = Mockito.mock(ServiceWrapper.class);
        Mockito.doReturn("MNP 1").when(serviceWrapper).getName();
        Mockito.doReturn(new DateTime()).when(serviceWrapper).getVaccineDate();
        serviceWrapperMap.put("MNP", serviceWrapper);

        Alert alert = Mockito.mock(Alert.class);
        Mockito.doReturn("2019-01-01").when(alert).startDate();
        Mockito.doReturn(alert).when(serviceWrapper).getAlert();

        Mockito.doReturn("My name").when(context).getString(Mockito.anyInt(), Mockito.any());


        ReflectionHelpers.callInstanceMethod(interactor, "evaluateMNP"
                , ReflectionHelpers.ClassParameter.from(Map.class, serviceWrapperMap));

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateBirthCert() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateBirthCertForm");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateMUAC() {
        Date dob = LocalDate.now().minusDays(280).toDate();
        Whitebox.setInternalState(interactor, "dob", dob);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateMUAC");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateDietary() {
        Date dob = LocalDate.now().minusDays(280).toDate();
        Whitebox.setInternalState(interactor, "dob", dob);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDietary");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateECD() {
        ReflectionHelpers.callInstanceMethod(interactor, "evaluateECD");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateLLITN() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateLLITN");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testEvaluateObsAndIllness() {
        Whitebox.setInternalState(interactor, "hasBirthCert", false);

        ReflectionHelpers.callInstanceMethod(interactor, "evaluateObsAndIllness");

        Mockito.verify(actionList).put(Mockito.anyString(), Mockito.any(BaseAncHomeVisitAction.class));
    }

    @Test
    public void testGetAgeInMonths() {
        int months = ReflectionHelpers.callInstanceMethod(interactor, "getAgeInMonths");
        assertEquals(2, months);
    }

    private String getVaccine() {
        return "[\n" +
                "  {\n" +
                "    \"name\": \"Birth\",\n" +
                "    \"id\": \"Birth\",\n" +
                "    \"days_after_birth_due\": 0,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"OPV 0\",\n" +
                "        \"type\": \"OPV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 0\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+0d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+28d\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"BCG\",\n" +
                "        \"type\": \"BCG\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+0d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"6 Weeks\",\n" +
                "    \"id\": \"Six_Wks\",\n" +
                "    \"days_after_birth_due\": 42,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"OPV 1\",\n" +
                "        \"type\": \"OPV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+42d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Penta 1\",\n" +
                "        \"type\": \"Penta\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+42d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"PCV 1\",\n" +
                "        \"type\": \"PCV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+42d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Rota 1\",\n" +
                "        \"type\": \"Rota\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+42d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"10 Weeks\",\n" +
                "    \"id\": \"Ten_Wks\",\n" +
                "    \"days_after_birth_due\": 70,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"OPV 2\",\n" +
                "        \"type\": \"OPV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"OPV 1\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Penta 2\",\n" +
                "        \"type\": \"Penta\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"Penta 1\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"PCV 2\",\n" +
                "        \"type\": \"PCV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"PCV 1\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Rota 2\",\n" +
                "        \"type\": \"Rota\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"Rota 1\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"14 Weeks\",\n" +
                "    \"id\": \"Fourteen_Weeks\",\n" +
                "    \"days_after_birth_due\": 98,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"OPV 3\",\n" +
                "        \"type\": \"OPV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 3\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"OPV 2\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Penta 3\",\n" +
                "        \"type\": \"Penta\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 3\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"Penta 2\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"PCV 3\",\n" +
                "        \"type\": \"PCV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 3\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"PCV 2\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Rota 3\",\n" +
                "        \"type\": \"Rota\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 3\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"prerequisite\",\n" +
                "              \"prerequisite\": \"Rota 2\",\n" +
                "              \"offset\": \"+28d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"IPV\",\n" +
                "        \"type\": \"IPV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"1422AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"1422AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+98d\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"9 Months\",\n" +
                "    \"id\": \"Nine_Months\",\n" +
                "    \"days_after_birth_due\": 274,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"MCV 1\",\n" +
                "        \"type\": \"MCV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"79409AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"79409AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+9m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Rubella 1\",\n" +
                "        \"type\": \"Rubella\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"83563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"83563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+9m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Yellow Fever\",\n" +
                "        \"type\": \"YF\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"5864AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"5864AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+9m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"MenA\",\n" +
                "        \"type\": \"MenA\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"79549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"79549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 1\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+9m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"15 Months\",\n" +
                "    \"id\": \"Fifteen_Months\",\n" +
                "    \"days_after_birth_due\": 456,\n" +
                "    \"vaccines\": [\n" +
                "      {\n" +
                "        \"name\": \"MCV 2\",\n" +
                "        \"type\": \"MCV\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"79409AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"79409AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+15m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Rubella 2\",\n" +
                "        \"type\": \"Rubella\",\n" +
                "        \"openmrs_date\": {\n" +
                "          \"parent_entity\": \"83563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"openmrs_calculate\": {\n" +
                "          \"parent_entity\": \"83563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"entity\": \"concept\",\n" +
                "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"calculation\": 2\n" +
                "        },\n" +
                "        \"schedule\": {\n" +
                "          \"due\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+15m\",\n" +
                "              \"window\": \"+14d\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"expiry\": [\n" +
                "            {\n" +
                "              \"reference\": \"dob\",\n" +
                "              \"offset\": \"+2y\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }
}
