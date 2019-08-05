package org.smartregister.chw.interactor;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.DangerSignsAction;
import org.smartregister.chw.actionhelper.ObservationAction;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.dao.PNCDao;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.domain.Person;
import org.smartregister.chw.rule.PNCHealthFacilityVisitRule;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.PNCVisitUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public abstract class DefaultPncHomeVisitInteractorFlv implements PncHomeVisitInteractor.Flavor {

    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;
    protected Context context;
    protected Map<String, List<VisitDetail>> details = null;
    protected List<Person> children;
    protected MemberObject memberObject;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.PNC_HOME_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        children = PersonDao.getMothersChildren(memberObject.getBaseEntityId());
        if (children == null)
            children = new ArrayList<>();

        try {
            evaluateDangerSignsMother();
            evaluateDangerSignsBaby();
            evaluatePNCHealthFacilityVisit();
            //evaluateChildVaccineCard();
            //evaluateImmunization();
            evaluateUmbilicalCord();
            evaluateExclusiveBreastFeeding();
            evaluateKangarooMotherCare();
            evaluateFamilyPlanning();
            evaluateObservationAndIllnessMother();
            evaluateObservationAndIllnessBaby();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
        return actionList;
    }

    private void evaluateDangerSignsMother() throws Exception {
        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_danger_signs_mother))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsMother())
                .withHelper(new DangerSignsAction())
                .build();
        actionList.put(context.getString(R.string.pnc_danger_signs_mother), action);
    }

    private void evaluateDangerSignsBaby() throws Exception {
        for (Person baby : children) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_danger_signs_baby), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsBaby())
                    .withHelper(new DangerSignsAction())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_danger_signs_baby), baby), action);
        }
    }

    protected void evaluatePNCHealthFacilityVisit() throws Exception {

        PNCHealthFacilityVisitSummary summary = PNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
        if (summary != null) {
            PNCHealthFacilityVisitRule visitRule = PNCVisitUtil.getNextPNCHealthFacilityVisit(summary.getDeliveryDate(), summary.getLastVisitDate());

            if (visitRule != null && visitRule.getVisitName() != null) {

                int visit_num;
                switch (visitRule.getVisitName()) {
                    case "1":
                        visit_num = 1;
                        break;
                    case "7":
                        visit_num = 7;
                        break;
                    case "1000":
                        visit_num = 42;
                        break;
                    default:
                        visit_num = 1;
                        break;
                }

                for (Person baby : children) {
                    BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_health_facility_visit), visitRule.getVisitName(), baby.getFullName()))
                            .withOptional(false)
                            .withDetails(details)
                            .withBaseEntityID(baby.getBaseEntityID())
                            .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getHealthFacilityVisit())
                            .withHelper(new PNCHealthFacilityVisitHelper(visitRule, baby, visit_num))
                            .build();
                    actionList.put(MessageFormat.format(context.getString(R.string.pnc_health_facility_visit), visitRule.getVisitName(), baby.getFullName()), action);
                }
            }
        }
    }

    protected void evaluateChildVaccineCard() throws Exception {
        for (Person baby : children) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_child_vaccine_card_recevied), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSigns())
                    .withHelper(new DangerSignsAction())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_child_vaccine_card_recevied), baby.getFullName()), action);
        }
    }

    protected void evaluateImmunization() throws Exception {
        for (Person baby : children) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_immunization_at_birth), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSigns())
                    .withHelper(new DangerSignsAction())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_immunization_at_birth), baby.getFullName()), action);
        }
    }

    private void evaluateUmbilicalCord() throws Exception {
        HomeVisitActionHelper umbilicalCordHelper = new HomeVisitActionHelper() {
            private String cord_care;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    cord_care = JsonFormUtils.getValue(jsonObject, "cord_care");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", "Cord Care", cord_care);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isNotBlank(cord_care)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_umblicord_care))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getUmbilicalCord())
                .withHelper(umbilicalCordHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_umblicord_care), action);
    }

    private void evaluateExclusiveBreastFeeding() throws Exception {
        for (Person baby : children) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_exclusive_breastfeeding), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSigns())
                    .withHelper(new DangerSignsAction())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_exclusive_breastfeeding), baby.getFullName()), action);
        }
    }

    private void evaluateKangarooMotherCare() throws Exception {
        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_kangeroo_mother_care))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSigns())
                .withHelper(new DangerSignsAction())
                .build();
        actionList.put(context.getString(R.string.pnc_kangeroo_mother_care), action);
    }

    private void evaluateFamilyPlanning() throws Exception {
        HomeVisitActionHelper helper = new HomeVisitActionHelper() {
            private String fp_counseling;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    fp_counseling = JsonFormUtils.getValue(jsonObject, "fp_counseling");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", "Family Planning ", fp_counseling);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isNotBlank(fp_counseling)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_family_planning))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getFamilyPlanning())
                .withHelper(helper)
                .build();
        actionList.put(context.getString(R.string.pnc_family_planning), action);
    }

    private void evaluateObservationAndIllnessMother() throws Exception {
        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_observation_and_illness_mother))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getObservationAndIllness())
                .withHelper(new ObservationAction())
                .build();
        actionList.put(context.getString(R.string.pnc_observation_and_illness_mother), action);
    }

    private void evaluateObservationAndIllnessBaby() throws Exception {
        for (Person baby : children) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_observation_and_illness_baby), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getObservationAndIllness())
                    .withHelper(new ObservationAction())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_observation_and_illness_baby), baby), action);
        }
    }

    private class PNCHealthFacilityVisitHelper implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private Context context;
        private String jsonPayload;

        private PNCHealthFacilityVisitRule visitRule;
        private Person baby;
        private int visit_num;

        public PNCHealthFacilityVisitHelper(PNCHealthFacilityVisitRule visitRule, Person baby, int visit_num) {
            this.visitRule = visitRule;
            this.baby = baby;
            this.visit_num = visit_num;
        }

        @Override
        public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
            this.jsonPayload = jsonPayload;
        }

        @Override
        public String getPreProcessed() {

            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                JSONArray fields = fields(jsonObject);


                String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString(JsonFormConstants.STEP_TITLE);
                jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, visitRule.getVisitName()));

                JSONObject pnc_visit = getFieldJSONObject(fields, "pnc_visit_{0}");

                /*
                pnc_visit.put(JsonFormConstants.KEY, visitRule.getVisitName());

                JSONObject pnc_hf_visit_date = getFieldJSONObject(fields, "pnc_hf_visit{0}_date}");
                pnc_hf_visit_date.put(JsonFormConstants.KEY, visitRule.getVisitName());
                pnc_hf_visit_date.put(JsonFormConstants.KEY, visitRule.getVisitName());
                */

                return jsonObject.toString();
            } catch (Exception e) {
                Timber.e(e);
            }
            return null;
        }

        @Override
        public void onPayloadReceived(String s) {

        }

        @Override
        public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
            return null;
        }

        @Override
        public String getPreProcessedSubTitle() {
            return null;
        }

        @Override
        public String postProcess(String s) {
            return null;
        }

        @Override
        public String evaluateSubTitle() {
            return null;
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            return null;
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {

        }
    }
}
