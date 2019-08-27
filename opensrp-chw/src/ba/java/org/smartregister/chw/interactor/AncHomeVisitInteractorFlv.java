package org.smartregister.chw.interactor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.HealthFacilityVisitAction;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.ContactUtil;
import org.smartregister.chw.util.JsonFormUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {
    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        Map<String, List<VisitDetail>> details = null;
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.ANC_HOME_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        // get contact
        LocalDate lastContact = new DateTime(memberObject.getDateCreated()).toLocalDate();
        boolean isFirst = (StringUtils.isBlank(memberObject.getLastContactVisit()));
        LocalDate lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastMenstrualPeriod());

        if (StringUtils.isNotBlank(memberObject.getLastContactVisit())) {
            lastContact = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastContactVisit());
        }

        Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();

        // today is the due date for the very first visit
        if (isFirst) {
            dateMap.put(0, LocalDate.now());
        }

        dateMap.putAll(ContactUtil.getContactWeeks(isFirst, lastContact, lastMenstrualPeriod));

        evaluateDangerSigns(actionList, details, context);
        evaluateHealthFacilityVisit(actionList, details, memberObject, dateMap, context);
        evaluateFamilyPlanning(actionList, details, context);
        evaluateNutritionStatus(actionList, details, context);
        evaluateCounsellingStatus(actionList, details, context);
        evaluateMalaria(actionList, details, context);
        evaluateObservation(actionList, details, context);
        evaluateRemarks(actionList, details, context);

        return actionList;
    }

    private void evaluateDangerSigns(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                     Map<String, List<VisitDetail>> details,
                                     final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction danger_signs = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_danger_signs))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getDangerSigns())
                .withHelper(new DangerSignsAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_danger_signs), danger_signs);
    }

    private void evaluateHealthFacilityVisit(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                             Map<String, List<VisitDetail>> details,
                                             final MemberObject memberObject,
                                             Map<Integer, LocalDate> dateMap,
                                             final Context context) throws BaseAncHomeVisitAction.ValidationException {
        String visit_title = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1);
        BaseAncHomeVisitAction facility_visit = new BaseAncHomeVisitAction.Builder(context, visit_title)
                .withOptional(false)
                .withDetails(details)
                .withHelper(new HealhFacilityAction(memberObject, dateMap))
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getHealthFacilityVisit())
                .build();

        actionList.put(visit_title, facility_visit);
    }

    private void evaluateFamilyPlanning(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                        Map<String, List<VisitDetail>> details,
                                        final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction family_planning_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_family_planning))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getFamilyPlanning())
                .withHelper(new FamilyPlanningAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_family_planning), family_planning_ba);
    }

    private void evaluateNutritionStatus(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                         Map<String, List<VisitDetail>> details,
                                         final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction nutrition_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_nutrition_status))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getNutritionStatus())
                .withHelper(new NutritionAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_nutrition_status), nutrition_ba);
    }

    private void evaluateCounsellingStatus(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                           Map<String, List<VisitDetail>> details,
                                           final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction counselling_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_counselling_task))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getCOUNSELLING())
                .withHelper(new CounsellingStatusAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_counselling_task), counselling_ba);
    }

    private void evaluateMalaria(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                 Map<String, List<VisitDetail>> details,
                                 final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction malaria_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_malaria_prevention))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getMALARIA())
                .withHelper(new MalariaAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_malaria_prevention), malaria_ba);
    }

    private void evaluateObservation(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                     Map<String, List<VisitDetail>> details,
                                     final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction remark_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_observations_n_illnes))
                .withOptional(true)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getObservationAndIllness())
                .withHelper(new ObservationAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), remark_ba);
    }

    private void evaluateRemarks(LinkedHashMap<String, BaseAncHomeVisitAction> actionList,
                                 Map<String, List<VisitDetail>> details,
                                 final Context context) throws BaseAncHomeVisitAction.ValidationException {
        BaseAncHomeVisitAction remark_ba = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_remarks_and_comments))
                .withOptional(true)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getRemarksAndComments())
                .withHelper(new RemarksAction())
                .build();
        actionList.put(context.getString(R.string.anc_home_visit_remarks_and_comments), remark_ba);
    }


    private class DangerSignsAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private String danger_signs_counseling;
        private String danger_signs_present;
        private Context context;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                danger_signs_counseling = JsonFormUtils.getValue(jsonObject, "danger_signs_counseling");
                danger_signs_present = JsonFormUtils.getCheckBoxValue(jsonObject, "danger_signs_present");
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            return MessageFormat.format("Danger signs: {0}", danger_signs_present) +
                    "\n" +
                    MessageFormat.format("Health facility counselling {0}",
                            (danger_signs_counseling.equalsIgnoreCase("Yes") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase())
                    );
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(danger_signs_counseling)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if (danger_signs_counseling.equalsIgnoreCase("Yes")) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            } else if (danger_signs_counseling.equalsIgnoreCase("No")) {
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            } else {
                return BaseAncHomeVisitAction.Status.PENDING;
            }
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class HealhFacilityAction extends HealthFacilityVisitAction {
        private Context context;

        private String anc_hf_visit;
        private String anc_hf_visit_date;
        private String tests_done;
        private String imm_medicine_given;
        private String llin_given;
        private Date visitDate;


        public HealhFacilityAction(MemberObject memberObject, Map<Integer, LocalDate> dateMap) {
            super(memberObject, dateMap);
        }

        @Override
        public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
            super.onJsonFormLoaded(jsonPayload, context, map);
            this.context = context;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);

                anc_hf_visit = JsonFormUtils.getValue(jsonObject, "anc_hf_visit");
                anc_hf_visit_date = JsonFormUtils.getValue(jsonObject, "anc_hf_visit_date");
                tests_done = JsonFormUtils.getCheckBoxValue(jsonObject, "tests_done");
                imm_medicine_given = JsonFormUtils.getCheckBoxValue(jsonObject, "imm_medicine_given");
                llin_given = JsonFormUtils.getValue(jsonObject, "llin_given");

                visitDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(anc_hf_visit_date);

            } catch (Exception e) {
                Timber.e(e);
            }
        }

        @Override
        public String evaluateSubTitle() {
            StringBuilder stringBuilder = new StringBuilder();
            if (anc_hf_visit.equalsIgnoreCase("No")) {
                stringBuilder.append(context.getString(R.string.visit_not_done).replace("\n", ""));
            } else {
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(visitDate)));
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.tests_done), tests_done));
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.treatment_given), imm_medicine_given));
                stringBuilder.append(MessageFormat.format("{0}: {1}", context.getString(R.string.received_llin), llin_given));
            }
            return stringBuilder.toString();
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(anc_hf_visit)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if (anc_hf_visit.equalsIgnoreCase("Yes")) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            } else {
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            }
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class FamilyPlanningAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private Context context;
        private String fam_planning;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                fam_planning = JsonFormUtils.getValue(jsonObject, "fam_planning").toLowerCase();
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            String subTitle = (fam_planning.equalsIgnoreCase("Yes") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase());
            return StringUtils.capitalize(subTitle);
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(fam_planning)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if (fam_planning.equalsIgnoreCase("Yes")) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            } else {
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            }
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class NutritionAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private Context context;
        private String nutrition_status;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                nutrition_status = JsonFormUtils.getValue(jsonObject, "nutrition_status").toLowerCase();
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            return MessageFormat.format("{0}: {1}", context.getString(R.string.nutrition_status), StringUtils.capitalize(nutrition_status));
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(nutrition_status)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class CounsellingStatusAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private Context context;
        private String counselling_given;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                counselling_given = JsonFormUtils.getCheckBoxValue(jsonObject, "counselling_given").toLowerCase();
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            String subTitle = (!counselling_given.contains("none") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase());
            return MessageFormat.format("{0} {1}", context.getString(R.string.counselling), subTitle);
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(counselling_given)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class MalariaAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private String fam_llin;
        private String llin_2days;
        private String llin_condition;
        private Context context;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                fam_llin = JsonFormUtils.getValue(jsonObject, "fam_llin");
                llin_2days = JsonFormUtils.getValue(jsonObject, "llin_2days");
                llin_condition = JsonFormUtils.getValue(jsonObject, "llin_condition");
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            StringBuilder stringBuilder = new StringBuilder();
            if (fam_llin.equalsIgnoreCase("No")) {
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.uses_net), StringUtils.capitalize(fam_llin.trim().toLowerCase())));
            } else {

                stringBuilder.append(MessageFormat.format("{0}: {1} · ", context.getString(R.string.uses_net), StringUtils.capitalize(fam_llin.trim().toLowerCase())));
                stringBuilder.append(MessageFormat.format("{0}: {1} · ", context.getString(R.string.slept_under_net), StringUtils.capitalize(llin_2days.trim().toLowerCase())));
                stringBuilder.append(MessageFormat.format("{0}: {1}", context.getString(R.string.net_condition), StringUtils.capitalize(llin_condition.trim().toLowerCase())));
            }

            return stringBuilder.toString();
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(fam_llin)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if (fam_llin.equalsIgnoreCase("Yes") && llin_2days.equalsIgnoreCase("Yes") && llin_condition.equalsIgnoreCase("Okay")) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            } else {
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            }
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class ObservationAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private String date_of_illness;
        private String illness_description;
        private String action_taken;
        private Context context;
        private LocalDate illnessDate;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                date_of_illness = JsonFormUtils.getValue(jsonObject, "date_of_illness");
                illness_description = JsonFormUtils.getValue(jsonObject, "illness_description");
                action_taken = JsonFormUtils.getCheckBoxValue(jsonObject, "action_taken");
                illnessDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(date_of_illness);
            } catch (Exception e) {
                Timber.e(e);
            }
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
            if (illnessDate == null) {
                return "";
            }

            return MessageFormat.format("{0}: {1}\n {2}: {3}",
                    DateTimeFormat.forPattern("dd MMM yyyy").print(illnessDate),
                    illness_description, context.getString(R.string.action_taken), action_taken
            );
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(date_of_illness)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }

    private class RemarksAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private String chw_comment_anc;
        private Context context;

        @Override
        public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
            this.context = context;
        }

        @Override
        public String getPreProcessed() {
            return null;
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                chw_comment_anc = JsonFormUtils.getValue(jsonObject, "chw_comment_anc");
            } catch (JSONException e) {
                Timber.e(e);
            }
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
            return MessageFormat.format("{0}: {1}",
                    context.getString(R.string.remarks_and__comments), StringUtils.capitalize(chw_comment_anc));
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(chw_comment_anc)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.v("onPayloadReceived");
        }
    }
}
