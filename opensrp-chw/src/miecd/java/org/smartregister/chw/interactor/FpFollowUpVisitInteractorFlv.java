package org.smartregister.chw.interactor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpAlertObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class FpFollowUpVisitInteractorFlv extends DefaultFpFollowUpVisitInteractorFlv {
    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;
    protected Context context;
    protected Map<String, List<VisitDetail>> details = null;
    protected List<PncBaby> children;
    protected MemberObject memberObject;
    protected BaseAncHomeVisitContract.View view;
    protected Boolean editMode = false;
    protected String familyPlanningMethod;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        editMode = view.getEditMode();
        this.view = view;
        List<FpAlertObject> memberDetails = FpDao.getFpDetails(memberObject.getBaseEntityId());
        if (memberDetails.size() > 0) {
            for (FpAlertObject detail : memberDetails) {
                familyPlanningMethod = detail.getFpMethod();
            }
        }
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT);
            if (lastVisit != null) {
                details = Collections.unmodifiableMap(VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId())));
            }
        }
        try {
            Constants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
            evaluateSideEffects();
            evaluateCounselling();
            evaluateResupply();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
        return actionList;
    }

    private void evaluateSideEffects() throws Exception {

        Map<String, List<VisitDetail>> details = null;
        if (editMode) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT_SIDE_EFFECTS);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        JSONObject jsonObject = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupSideEffects());
        injectFamilyPlaningMethod(jsonObject);
        // jsonObject
        if (details != null && details.size() > 0) {
            org.smartregister.chw.anc.util.JsonFormUtils.populateForm(jsonObject, details);
        }
        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.fp_side_effects))
                .withOptional(false)
                .withDetails(details)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withHelper(new SideEffectsHelper())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withFormName(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupSideEffects())
                .withJsonPayload(jsonObject.toString())
                .build();

        actionList.put(context.getString(R.string.fp_side_effects), action);
    }

    private void evaluateCounselling() throws Exception {

        Map<String, List<VisitDetail>> details = null;
        if (editMode) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT_COUNSELLING);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.counseling))
                .withOptional(false)
                .withDetails(details)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withHelper(new CounsellingHelper())
                .withFormName(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupCounsel())
                .build();

        actionList.put(context.getString(R.string.counseling), action);
    }

    private void evaluateResupply() throws Exception {
        String familyPlanningMethodTranslated = null;
        switch (familyPlanningMethod){
            case "COC":
                familyPlanningMethodTranslated = context.getString(R.string.coc);
                break;
            case "POP":
                familyPlanningMethodTranslated = context.getString(R.string.pop);
                break;
            case "Female sterilization":
                familyPlanningMethodTranslated = context.getString(R.string.female_sterilization);
                break;
            case "Injectable":
                familyPlanningMethodTranslated = context.getString(R.string.injectable);
                break;
            case "Male condom":
                familyPlanningMethodTranslated = context.getString(R.string.male_condom);
                break;
            case "Female condom":
                familyPlanningMethodTranslated = context.getString(R.string.female_condom);
                break;
            case "IUCD":
                familyPlanningMethodTranslated = context.getString(R.string.iucd);
                break;
            default:
                familyPlanningMethodTranslated = null;
                break;
        }

        if (!familyPlanningMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_FEMALE_STERLIZATION) && !familyPlanningMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_IUCD)) {
            Map<String, List<VisitDetail>> details = null;
            if (editMode) {
                Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT_RESUPPLY);
                if (lastVisit != null) {
                    details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
                }
            }

            JSONObject jsonObject = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupResupply());
            injectFamilyPlaningMethod(jsonObject);
            // jsonObject
            if (details != null && details.size() > 0) {
                org.smartregister.chw.anc.util.JsonFormUtils.populateForm(jsonObject, details);
            }

            if (!familyPlanningMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
                BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.resupply, familyPlanningMethod))
                        .withOptional(false)
                        .withDetails(details)
                        .withBaseEntityID(memberObject.getBaseEntityId())
                        .withHelper(new ResupplyHelper())
                        .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                        .withFormName(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupResupply())
                        .withJsonPayload(jsonObject.toString())
                        .build();

                actionList.put(context.getString(R.string.resupply, familyPlanningMethodTranslated), action);
            } else {
                BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.next_health_facility_visit))
                        .withOptional(false)
                        .withDetails(details)
                        .withBaseEntityID(memberObject.getBaseEntityId())
                        .withHelper(new ResupplyHelper())
                        .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                        .withFormName(Constants.JSON_FORM.FamilyPlanningFollowUpVisitUtils.getFamilyPlanningFollowupResupply())
                        .withJsonPayload(jsonObject.toString())
                        .build();

                actionList.put(context.getString(R.string.next_health_facility_visit), action);
            }

        }
    }

    public JSONObject injectFamilyPlaningMethod(JSONObject form) throws Exception {
        if (form == null) {
            return null;
        } else {
            JSONArray field = fields(form);
            JSONObject datePass = getFieldJSONObject(field, "fp_method");
            datePass.put("value", familyPlanningMethod);
            return form;
        }
    }

    private class ResupplyHelper extends HomeVisitActionHelper {
        private String no_condoms;
        private String no_pillcycles;
        private String last_injection_date;
        private String nextInjectionDate;

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                no_condoms = JsonFormUtils.getValue(jsonObject, "no_condoms");
                no_pillcycles = JsonFormUtils.getValue(jsonObject, "no_pillcycles");
                last_injection_date = JsonFormUtils.getValue(jsonObject, "fp_refill_injectable");
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        @Override
        public String evaluateSubTitle() {
            String resupply = getNonBlankString(no_condoms, no_pillcycles, last_injection_date);

            if (StringUtils.isBlank(resupply)) {
                return null;
            }
            try {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                nextInjectionDate = sdf.format(((formatter.parseDateTime(last_injection_date).toLocalDate()).plusDays(84)).toDate());

            } catch (Exception e) {
                Timber.v(e.toString());
            }

            StringBuilder builder = new StringBuilder();
            if (resupply.equalsIgnoreCase(no_condoms)) {
                builder.append(context.getString(R.string.no_of_condoms)).append(" ").append(resupply);
            } else if (resupply.equalsIgnoreCase(no_pillcycles)) {
                builder.append(context.getString(R.string.no_of_pill_cycles)).append(" ").append(resupply);

            } else if (resupply.equalsIgnoreCase(last_injection_date)) {
                builder.append(context.getString(R.string.date_of_next_injection)).append(" ").append(nextInjectionDate);

            }

            return builder.toString();
        }

        private String getNonBlankString(String... strings) {
            if (strings == null || strings.length == 0)
                return "";

            for (String s : strings) {
                if (StringUtils.isNotBlank(s)) return s;
            }
            return "";
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(no_condoms) && StringUtils.isBlank(no_pillcycles) && StringUtils.isBlank(last_injection_date)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }
            if("0".equalsIgnoreCase(no_pillcycles) || "0".equalsIgnoreCase(no_condoms))
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;

            if (!StringUtils.isBlank(no_condoms) || !StringUtils.isBlank(no_pillcycles) || !StringUtils.isBlank(last_injection_date)) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            }
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }

    private class SideEffectsHelper extends HomeVisitActionHelper {
        private String condom_side_effects;
        private String condom_side_effects_other;
        private String cocpop_side_effects;
        private String cocpop_side_effects_other;
        private String inject_side_effects;
        private String inject_side_effects_other;
        private String IUCD_side_effects;
        private String IUCD_side_effects_other;
        private String sterilization_side_effects;
        private String sterilization_side_effects_other;
        private String action_taken;
        private String sideEffects;
        private String other;


        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                condom_side_effects = JsonFormUtils.getValue(jsonObject, "condom_side_effects");
                condom_side_effects_other = JsonFormUtils.getValue(jsonObject, "condom_side_effects_other");
                cocpop_side_effects = JsonFormUtils.getValue(jsonObject, "cocpop_side_effects");
                cocpop_side_effects_other = JsonFormUtils.getValue(jsonObject, "cocpop_side_effects_other");
                inject_side_effects = JsonFormUtils.getValue(jsonObject, "inject_side_effects");
                inject_side_effects_other = JsonFormUtils.getValue(jsonObject, "inject_side_effects_other");
                IUCD_side_effects = JsonFormUtils.getValue(jsonObject, "IUCD_side_effects");
                IUCD_side_effects_other = JsonFormUtils.getValue(jsonObject, "IUCD_side_effects_other");
                sterilization_side_effects = JsonFormUtils.getValue(jsonObject, "sterilization_side_effects");
                sterilization_side_effects_other = JsonFormUtils.getValue(jsonObject, "sterilization_side_effects_other");
                action_taken = JsonFormUtils.getValue(jsonObject, "action_taken");
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        private String evaluateCondomSideEffects(){
            String condomSideEffects;

            switch (condom_side_effects) {
                case "Allergic_reactions":
                    condomSideEffects = context.getString(R.string.allergic_reactions);
                    break;
                case "Others":
                    condomSideEffects = context.getString(R.string.others);
                    break;
                case "None":
                    condomSideEffects = context.getString(R.string.none);
                    break;
                default:
                    condomSideEffects = "";
                    break;
            }
            return condomSideEffects;
        }

        private String evaluateCocPopSideEffects(){
            String cocpopSideEffects;
            switch (cocpop_side_effects) {
                case "Heavy_bleeding_":
                    cocpopSideEffects = context.getString(R.string.heavy_bleading);
                    break;
                case "Irregular_periods":
                    cocpopSideEffects = context.getString(R.string.irregular_period);
                    break;
                case "Others":
                    cocpopSideEffects = context.getString(R.string.others);
                    break;
                case "None":
                    cocpopSideEffects = context.getString(R.string.none);
                    break;
                default:
                    cocpopSideEffects = "";
                    break;
            }
            return cocpopSideEffects;
        }

        private String evaluateInjectSideEffects(){
            String injectSideEffects;
            switch (inject_side_effects) {
                case "Heavy_bleeding_":
                    injectSideEffects = context.getString(R.string.heavy_bleading);
                    break;
                case "Irregular_periods":
                    injectSideEffects = context.getString(R.string.irregular_period);
                    break;
                case "Others":
                    injectSideEffects = context.getString(R.string.others);
                    break;
                case "None":
                    injectSideEffects = context.getString(R.string.none);
                    break;
                default:
                    injectSideEffects = "";
                    break;
            }
            return injectSideEffects;
        }

        private String evaluateIUCDSideEffects(){
            String iucdSideEffects;
            switch (IUCD_side_effects) {
                case "Severe_pain_inside_the_vagina_after_IUD_was_put_in":
                    iucdSideEffects = context.getString(R.string.severe_pain_inside_the_vagina);
                    break;
                case "Cramping_or_backaches_for_a_few_days_after_the_IUD_is_put_in":
                    iucdSideEffects = context.getString(R.string.cramps_or_back_ache);
                    break;
                case "Spotting_between_periods_and_or_irregular_periods":
                    iucdSideEffects = context.getString(R.string.spotting);
                    break;
                case "Heavier_periods_and_worse_menstrual_cramps":
                    iucdSideEffects = context.getString(R.string.heavier_period);
                    break;
                case "IUCD_explusion":
                    iucdSideEffects = context.getString(R.string.IUCD_explusion);
                    break;
                case "Others":
                    iucdSideEffects = context.getString(R.string.others);
                    break;
                case "None":
                    iucdSideEffects = context.getString(R.string.none);
                    break;
                default:
                    iucdSideEffects ="";
                    break;
            }
            return iucdSideEffects;

        }

        private String evaluateSterilizationSideEffects(){
            String sterilizationSideEffects;
            switch (sterilization_side_effects) {
                case "Incisional_bleeding":
                    sterilizationSideEffects = context.getString(R.string.incisional_bleeding);
                    break;
                case "Pus_Discharge_from_incision":
                    sterilizationSideEffects = context.getString(R.string.pus_discharge_from_incision);
                    break;
                case "Swollen_around_the_incision":
                    sterilizationSideEffects = context.getString(R.string.swollen_around_the_incision);
                    break;
                case "Others":
                    sterilizationSideEffects = context.getString(R.string.others);
                    break;
                case "None":
                    sterilizationSideEffects = context.getString(R.string.none);
                    break;
                default:
                    sterilizationSideEffects = "";
                    break;
            }
            return sterilizationSideEffects;
        }

        private String evaluateActionTaken(){
            String actionTaken;

            switch (action_taken) {
                case "managed":
                    actionTaken = context.getString(R.string.managed);
                    break;
                case "referred":
                    actionTaken = context.getString(R.string.referred);
                    break;
                case "no_action_taken":
                    actionTaken = context.getString(R.string.no_action_taken);
                    break;
                default:
                    actionTaken = "";
                    break;
            }
            return actionTaken;
        }


        @Override
        public String evaluateSubTitle() {

            sideEffects = getNonBlankString(evaluateCondomSideEffects(), evaluateCocPopSideEffects(), evaluateInjectSideEffects(), evaluateIUCDSideEffects(), evaluateSterilizationSideEffects());
            other = getNonBlankString(condom_side_effects_other, cocpop_side_effects_other, inject_side_effects_other, IUCD_side_effects_other, sterilization_side_effects_other);

            if (StringUtils.isBlank(sideEffects)) {
                return null;
            }


            StringBuilder builder = new StringBuilder(context.getString(R.string.side_effects)).append(sideEffects);
            if (StringUtils.isNotBlank(other)) builder.append(" ").append(other);

            if (StringUtils.isNotBlank(action_taken)) {
                builder.append("\n");
                builder.append(context.getString(R.string.action)).append(evaluateActionTaken());
            }

            return builder.toString();
        }

        private String getNonBlankString(String... strings) {
            if (strings == null || strings.length == 0)
                return "";

            for (String s : strings) {
                if (StringUtils.isNotBlank(s)) return s;
            }
            return "";
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(action_taken) && StringUtils.isBlank(sideEffects) && StringUtils.isBlank(other)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }
            if (StringUtils.isBlank(action_taken) || !"no_action_taken".equalsIgnoreCase(action_taken)) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            }
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }

    private class CounsellingHelper extends HomeVisitActionHelper {
        private String fp_counselling;

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                fp_counselling = JsonFormUtils.getValue(jsonObject, "fp_counselling");
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        @Override
        public String evaluateSubTitle() {
            if (StringUtils.isBlank(fp_counselling)) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            if (fp_counselling.equalsIgnoreCase("yes"))
                builder.append(context.getString(R.string.counseling)).append(":").append(" ").append(context.getString(R.string.yes));
            else if (fp_counselling.equalsIgnoreCase("no")) {
                builder.append(context.getString(R.string.counseling)).append(":").append(" ").append(context.getString(R.string.no));
            }
            return builder.toString();
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(fp_counselling)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }
            if ("yes".equalsIgnoreCase(fp_counselling))
                return BaseAncHomeVisitAction.Status.COMPLETED;

            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }


}
