package org.smartregister.chw.interactor;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.ImmunizationActionHelper;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.fragment.BaseHomeVisitImmunizationFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.domain.Person;
import org.smartregister.chw.core.rule.PNCHealthFacilityVisitRule;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.dao.ChwPNCDaoFlv;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.PNCVisitUtil;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PncHomeVisitInteractorFlv extends DefaultPncHomeVisitInteractorFlv {
    protected List<Person> children;
    protected BaseAncHomeVisitContract.View view;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        editMode = view.getEditMode();
        this.view = view;

        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.PNC_HOME_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        children = PersonDao.getMothersChildren(memberObject.getBaseEntityId());
        if (children == null) {
            children = new ArrayList<>();
        }

        try {
            evaluateDangerSignsMother();
            evaluatePNCHealthFacilityVisit();
            evaluateFamilyPlanning();
            evaluateCounselling();
            evaluateMalariaPrevention();
            evaluateNutritionStatusMother();
            evaluateObsIllnessMother();

            for (Person baby : children) {
                evaluateDangerSignsBaby(baby);
                evaluateImmunization(baby);
                evaluateExclusiveBreastFeeding(baby);
                evaluateNutritionStatusBaby(baby);
                evaluateObsIllnessBaby(baby);
            }
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
        return actionList;
    }

    private void evaluateDangerSignsMother() throws Exception {

        HomeVisitActionHelper pncDangerSignsMotherHelper = new HomeVisitActionHelper() {
            private String danger_signs_present_mama;

            @Override
            public void onPayloadReceived(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    danger_signs_present_mama = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "danger_signs_present_mama");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", context.getString(R.string.pnc_danger_signs_mama), danger_signs_present_mama);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (danger_signs_present_mama == null) {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }

                if (StringUtils.isNotBlank(danger_signs_present_mama)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_danger_signs_mother))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsMother())
                .withHelper(pncDangerSignsMotherHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_danger_signs_mother), action);
    }

    private void evaluateDangerSignsBaby(Person baby) throws Exception {
        class PNCDangerSignsBabyHelper extends HomeVisitActionHelper {
            private String danger_signs_present_child;

            @Override
            public void onPayloadReceived(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    danger_signs_present_child = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "danger_signs_present_child");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", context.getString(R.string.pnc_danger_signs_baby_task), danger_signs_present_child);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isBlank(danger_signs_present_child)) {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }

                if (StringUtils.isNotBlank(danger_signs_present_child)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                }
            }
        }

        if (getAgeInDays(baby.getDob()) <= 28) {
            Map<String, List<VisitDetail>> details = null;
            if (editMode) {
                Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baby.getBaseEntityID(), Constants.EventType.DANGER_SIGNS_BABY);
                if (lastVisit != null) {
                    details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
                }
            }

            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_danger_signs_baby), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsBaby())
                    .withHelper(new PNCDangerSignsBabyHelper())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_danger_signs_baby), baby.getFullName()), action);
        }
    }

    private void evaluateFamilyPlanning() throws Exception {
        boolean hasFP = ChwPNCDaoFlv.hasFamilyPlanning(memberObject.getBaseEntityId());
        if (hasFP)
            return;

        HomeVisitActionHelper helper = new HomeVisitActionHelper() {
            private String fp_counseling;
            private String fp_method;
            private String fp_start_date;
            private Date start_date;
            private String fp_period_received;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    fp_counseling = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "fp_counseling");
                    fp_method = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "fp_method");
                    fp_start_date = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "fp_start_date");
                    fp_period_received = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "fp_period_received");

                    if (StringUtils.isNotBlank(fp_start_date)) {
                        start_date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(fp_start_date);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }

            private String evaluateFpPeriod() {
                if (fp_period_received != null) {
                    List<String> listFpPeriods = Arrays.asList(fp_period_received.replace("\"", "").replace("[", "").replace("]", "").split("\\s*,\\s*"));
                    if (listFpPeriods.size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        ArrayList<String> builderList = new ArrayList<>();
                        String SEPARATOR = ",";
                        String duringPeriod = "";
                        for (String period : listFpPeriods) {
                            switch (period) {
                                case "chk_during_anc":
                                    duringPeriod = context.getString(R.string.during_anc);
                                    break;
                                case "chk_during_labour_and_delivery":
                                    duringPeriod = context.getString(R.string.during_labour_and_delivery);
                                    break;
                                case "chk_during_pnc":
                                    duringPeriod = context.getString(R.string.during_pnc);
                                    break;
                                default:
                                    break;
                            }
                            builderList.add(duringPeriod);
                        }
                        for (String build : builderList) {
                            builder.append(build);
                            builder.append(SEPARATOR);
                        }
                        fp_period_received = builder.toString();
                        fp_period_received = fp_period_received.substring(0, fp_period_received.length() - SEPARATOR.length());
                    }
                }
                return fp_period_received;

            }

            @Override
            public String evaluateSubTitle() {
                StringBuilder builder = new StringBuilder();
                String subTitleText = MessageFormat.format("{0}: {1}\n",
                        context.getString(R.string.fp_counseling),
                        "Yes".equalsIgnoreCase(fp_counseling) ? evaluateFpPeriod() : context.getString(R.string.not_done).toLowerCase());
                builder.append(subTitleText);

                if (StringUtils.isNotBlank(fp_method)) {
                    String method = "";
                    switch (fp_method) {
                        case "None":
                            method = context.getString(R.string.none);
                            break;
                        case "PPIUCD":
                            method = context.getString(R.string.ppiucd);
                            break;
                        case "Pills":
                            method = context.getString(R.string.pills);
                            break;
                        case "Implant":
                            method = context.getString(R.string.implant);
                            break;
                        case "Condoms":
                            method = context.getString(R.string.condoms);
                            break;
                        case "LAM":
                            method = context.getString(R.string.lam);
                            break;
                        case "Bead Counting":
                            method = context.getString(R.string.standard_day_method);
                            break;
                        case "Permanent (BTL)":
                            method = context.getString(R.string.permanent_blt);
                            break;
                        case "Permanent (Vasectomy)":
                            method = context.getString(R.string.permanent_vasectomy);
                            break;
                        default:
                            break;
                    }
                    if (StringUtils.isBlank(fp_start_date) || fp_method.equals("None")) {
                        subTitleText = MessageFormat.format("{0}", method);
                    } else {
                        subTitleText = MessageFormat.format("{0}: {1}", method, new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(start_date));
                    }
                    builder.append(subTitleText);
                }
                return builder.toString();
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isBlank(fp_counseling)) {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
                if ("Yes".equalsIgnoreCase(fp_counseling)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
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

    private void evaluateExclusiveBreastFeeding(Person baby) throws Exception {
        class ExclusiveBreastFeedingHelper extends HomeVisitActionHelper {
            private String exclusive_breast_feeding;
            private Date dob;

            public ExclusiveBreastFeedingHelper(Date dob) {
                this.dob = dob;
            }

            @Override
            public String getPreProcessedSubTitle() {
                return MessageFormat.format("{0} {1}", context.getString(R.string.due), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dob));
            }

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    exclusive_breast_feeding = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "exclusive_breast_feeding");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                if (StringUtils.isBlank(exclusive_breast_feeding)) {
                    return "";
                }

                return "Yes".equalsIgnoreCase(exclusive_breast_feeding) ? context.getString(R.string.yes) : context.getString(R.string.no);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isBlank(exclusive_breast_feeding)) {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }

                if (exclusive_breast_feeding.equalsIgnoreCase("Yes")) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else if (exclusive_breast_feeding.equalsIgnoreCase("No")) {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        }
        if (getAgeInDays(baby.getDob()) <= 28) {
            Map<String, List<VisitDetail>> details = null;
            if (editMode) {
                Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baby.getBaseEntityID(), Constants.EventType.EXCLUSIVE_BREASTFEEDING);
                if (lastVisit != null) {
                    details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
                }
            }
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_exclusive_breastfeeding), baby.getFullName()))
                    .withOptional(false)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                    .withDestinationFragment(BaseAncHomeVisitFragment.getInstance(view, Constants.JSON_FORM.PNC_HOME_VISIT.getExclusiveBreastFeeding(), null, details, null))
                    .withHelper(new ExclusiveBreastFeedingHelper(baby.getDob()))
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_exclusive_breastfeeding), baby.getFullName()), action);
        }
    }

    private void evaluateCounselling() throws Exception {
        HomeVisitActionHelper counsellingHelper = new HomeVisitActionHelper() {
            private String couselling_pnc;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    couselling_pnc = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "couselling_pnc");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                String counsellingStatus = "None".equals(couselling_pnc) ? context.getString(R.string.subtask_not_done) : context.getString(R.string.subtask_done);
                return MessageFormat.format("{0}: {1}", context.getString(R.string.counselling), counsellingStatus);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isNotBlank(couselling_pnc)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_counselling))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getCOUNSELLING())
                .withHelper(counsellingHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_counselling), action);
    }

    private void evaluateNutritionStatusMother() throws Exception {
        HomeVisitActionHelper nutritionStatusMotherHelper = new HomeVisitActionHelper() {
            private String nutrition_status_mama;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    nutrition_status_mama = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "nutrition_status_mama");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", context.getString(R.string.mother_status), nutrition_status_mama);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isBlank(nutrition_status_mama)) {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }

                if (StringUtils.isNotBlank(nutrition_status_mama)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_nutrition_status))
                .withOptional(true)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getNutritionStatusMother())
                .withHelper(nutritionStatusMotherHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_nutrition_status), action);
    }

    private void evaluateNutritionStatusBaby(Person baby) throws Exception {

        class NutritionStatusBabyHelper extends HomeVisitActionHelper {
            private String nutrition_status_1m;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    nutrition_status_1m = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "nutrition_status_1m");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                return MessageFormat.format("{0}: {1}", context.getString(R.string.child_status), nutrition_status_1m);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isNotBlank(nutrition_status_1m)) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        }

        if (getAgeInDays(baby.getDob()) <= 28) {
            Map<String, List<VisitDetail>> details = null;
            if (editMode) {
                Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baby.getBaseEntityID(), Constants.EventType.NUTRITION_STATUS_BABY);
                if (lastVisit != null) {
                    details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
                }
            }

            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_nutrition_status_baby_name), baby.getFullName()))
                    .withOptional(true)
                    .withDetails(details)
                    .withBaseEntityID(baby.getBaseEntityID())
                    .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getNutritionStatusInfant())
                    .withHelper(new NutritionStatusBabyHelper())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_nutrition_status_baby_name), baby.getFullName()), action);
        }
    }

    private void evaluateMalariaPrevention() throws Exception {
        HomeVisitActionHelper malariaPreventionHelper = new HomeVisitActionHelper() {
            private String fam_llin;
            private String llin_2days;
            private String llin_condition;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    fam_llin = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "fam_llin");
                    llin_2days = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "llin_2days");
                    llin_condition = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "llin_condition");
                } catch (JSONException e) {
                    Timber.e(e);
                }
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
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_malaria_prevention))
                .withOptional(false)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getMalariaPrevention())
                .withHelper(malariaPreventionHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_malaria_prevention), action);
    }

    private void evaluateObsIllnessMother() throws Exception {
        HomeVisitActionHelper obsIllnessMotherHelper = new HomeVisitActionHelper() {
            private String date_of_illness;
            private String illness_description;
            private String action_taken;
            private LocalDate illnessDate;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    date_of_illness = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "date_of_illness_mama");
                    illness_description = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "illness_description_mama");
                    action_taken = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "action_taken_mama");
                    illnessDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(date_of_illness);
                } catch (Exception e) {
                    Timber.e(e);
                }
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
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_observation_and_illness_mother))
                .withOptional(true)
                .withDetails(details)
                .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getObservationAndIllnessMother())
                .withHelper(obsIllnessMotherHelper)
                .build();
        actionList.put(context.getString(R.string.pnc_observation_and_illness_mother), action);
    }

    private void evaluateObsIllnessBaby(Person baby) throws Exception {
        class ObsIllnessBabyHelper extends HomeVisitActionHelper {
            private String date_of_illness;
            private String illness_description;
            private String action_taken;
            private LocalDate illnessDate;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    date_of_illness = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "date_of_illness_child");
                    illness_description = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "illness_description_child");
                    action_taken = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "action_taken_child");
                    illnessDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(date_of_illness);
                } catch (Exception e) {
                    Timber.e(e);
                }
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
        }
        if (getAgeInDays(baby.getDob()) <= 28) {
            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_observation_and_illness_baby), baby.getFullName()))
                    .withOptional(true)
                    .withDetails(details)
                    .withFormName(Constants.JSON_FORM.PNC_HOME_VISIT.getObservationAndIllnessInfant())
                    .withHelper(new ObsIllnessBabyHelper())
                    .build();
            actionList.put(MessageFormat.format(context.getString(R.string.pnc_observation_and_illness_baby), baby.getFullName()), action);
        }
    }

    @Override
    protected void evaluatePNCHealthFacilityVisit() throws Exception {

        PNCHealthFacilityVisitSummary summary = ChwPNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
        if (summary != null) {
            PNCHealthFacilityVisitRule visitRule = PNCVisitUtil.getNextPNCHealthFacilityVisit(summary.getDeliveryDate(), summary.getLastVisitDate());

            if (visitRule != null && visitRule.getVisitName() != null) {
                String title;
                int visit_num;
                switch (visitRule.getVisitName()) {
                    case "3":
                        title = context.getString(R.string.pnc_health_facility_visit_days_three_to_seven);
                        visit_num = 2;
                        break;
                    case "8":
                        title = context.getString(R.string.pnc_health_facility_visit_days_eight_to_twenty_eight);
                        visit_num = 3;
                        break;
                    case "29":
                        title = context.getString(R.string.pnc_health_facility_visit_days_twenty_nine_to_forty_two);
                        visit_num = 4;
                        break;
                    default:
                        title = context.getString(R.string.pnc_health_facility_visit_within_fourty_eight_hours);
                        visit_num = 1;
                        break;
                }

                BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, title)
                        .withOptional(false)
                        .withDetails(details)
                        .withFormName(visit_num == 1 ? Constants.JSON_FORM.PNC_HOME_VISIT.getHealthFacilityVisit() : Constants.JSON_FORM.PNC_HOME_VISIT.getHealthFacilityVisitTwo())
                        .withHelper(new PNCHealthFacilityVisitHelper(visitRule, visit_num))
                        .build();
                actionList.put(title, action);
            }
        }
    }

    @Override
    protected void evaluateImmunization(Person baby) throws Exception {
        if (getAgeInDays(baby.getDob()) <= 28) {
            List<VaccineWrapper> wrappers = VaccineScheduleUtil.getChildDueVaccines(baby.getBaseEntityID(), baby.getDob(), 0);
            if (wrappers.size() > 0) {
                List<VaccineDisplay> displays = new ArrayList<>();
                for (VaccineWrapper vaccineWrapper : wrappers) {
                    VaccineDisplay display = new VaccineDisplay();
                    display.setVaccineWrapper(vaccineWrapper);
                    display.setStartDate(baby.getDob());
                    display.setEndDate(new Date());
                    displays.add(display);
                }

                Map<String, List<VisitDetail>> details = null;
                if (editMode) {
                    Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baby.getBaseEntityID(), Constants.EventType.IMMUNIZATION_VISIT);
                    if (lastVisit != null) {
                        details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
                    }
                }

                BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, MessageFormat.format(context.getString(R.string.pnc_immunization_at_birth), baby.getFullName()))
                        .withOptional(false)
                        .withDetails(details)
                        .withBaseEntityID(baby.getBaseEntityID())
                        .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                        .withDestinationFragment(BaseHomeVisitImmunizationFragment.getInstance(view, baby.getBaseEntityID(), details, displays))
                        .withHelper(new ImmunizationActionHelper(context, () -> wrappers))
                        .build();
                actionList.put(MessageFormat.format(context.getString(R.string.pnc_immunization_at_birth), baby.getFullName()), action);

            }
        }
    }

    private class PNCHealthFacilityVisitHelper implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
        private Context context;
        private String jsonPayload;

        private PNCHealthFacilityVisitRule visitRule;
        private int visit_num;

        private String pnc_visit;
        private String pnc_hf_visit_date;
        private String vit_a_mother;
        private String ifa_mother;

        private BaseAncHomeVisitAction.ScheduleStatus scheduleStatus;
        private String subTitle;
        private Date date;

        public PNCHealthFacilityVisitHelper(PNCHealthFacilityVisitRule visitRule, int visit_num) {
            this.visitRule = visitRule;
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
                scheduleStatus = (visitRule.getOverDueDate().toLocalDate().isBefore(LocalDate.now())) ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
                String due = (visitRule.getOverDueDate().toLocalDate().isBefore(LocalDate.now())) ? context.getString(R.string.overdue) : context.getString(R.string.due);

                subTitle = MessageFormat.format("{0} {1}", due, DateTimeFormat.forPattern("dd MMM yyyy").print(visitRule.getOverDueDate().toLocalDate()));
                JSONObject jsonObject = new JSONObject(jsonPayload);
                JSONArray fields = JsonFormUtils.fields(jsonObject);


                String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString(JsonFormConstants.STEP_TITLE);
                jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, visitRule.getVisitName()));

                JSONObject pnc_visit = JsonFormUtils.getFieldJSONObject(fields, "pnc_visit_{0}");
                pnc_visit.put(JsonFormConstants.KEY, MessageFormat.format("pnc_visit_{0}", visit_num));
                pnc_visit.put("hint",
                        MessageFormat.format(pnc_visit.getString(JsonFormConstants.HINT),
                                visit_num,
                                DateTimeFormat.forPattern("dd MMM yyyy").print(visitRule.getDueDate()
                                )
                        )
                );

                JSONObject pnc_visit_date = JsonFormUtils.getFieldJSONObject(fields, "pnc_hf_visit{0}_date");
                pnc_visit_date.put(JsonFormConstants.KEY, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));
                pnc_visit_date.put("hint",
                        MessageFormat.format(pnc_visit_date.getString(JsonFormConstants.HINT), visitRule.getVisitName())
                );
                updateObjectRelevance(pnc_visit_date);

                if (visit_num == 1) {
                    updateObjectRelevance(JsonFormUtils.getFieldJSONObject(fields, "vit_a_mother"));
                    updateObjectRelevance(JsonFormUtils.getFieldJSONObject(fields, "ifa_mother"));
                }

                return jsonObject.toString();
            } catch (Exception e) {
                Timber.e(e);
            }
            return null;
        }

        private void updateObjectRelevance(JSONObject jsonObject) throws JSONException {
            JSONObject relevance = jsonObject.getJSONObject(JsonFormConstants.RELEVANCE);
            JSONObject step = relevance.getJSONObject("step1:pnc_visit_{0}");
            relevance.put(MessageFormat.format("step1:pnc_visit_{0}", visit_num), step);
            relevance.remove("step1:pnc_visit_{0}");
        }

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                pnc_visit = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("pnc_visit_{0}", visit_num));
                pnc_hf_visit_date = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));

                if (visit_num == 1) {
                    vit_a_mother = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "vit_a_mother");
                    ifa_mother = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "ifa_mother");
                }

                if (StringUtils.isNotBlank(pnc_hf_visit_date)) {
                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(pnc_hf_visit_date);
                }
            } catch (JSONException e) {
                Timber.e(e);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        @Override
        public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
            return scheduleStatus;
        }

        @Override
        public String getPreProcessedSubTitle() {
            return subTitle;
        }

        @Override
        public String postProcess(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray field = JsonFormUtils.fields(jsonObject);
                JSONObject confirmed_visits = JsonFormUtils.getFieldJSONObject(field, "confirmed_health_facility_visits");
                JSONObject facility_visit_date = JsonFormUtils.getFieldJSONObject(field, "last_health_facility_visit_date");
                pnc_hf_visit_date = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));

                String count = String.valueOf(visit_num);
                String value = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("pnc_visit_{0}", visit_num));
                if (value.equalsIgnoreCase("Yes")) {
                    count = String.valueOf(visit_num + 1);
                    facility_visit_date.put(JsonFormConstants.VALUE, pnc_hf_visit_date);
                } else {
                    facility_visit_date.remove(JsonFormConstants.VALUE);
                }

                if (!confirmed_visits.getString(JsonFormConstants.VALUE).equals(count)) {
                    confirmed_visits.put(JsonFormConstants.VALUE, count);
                    return jsonObject.toString();
                }

            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        }

        @Override
        public String evaluateSubTitle() {
            if ("No".equals(pnc_visit)) {
                return context.getString(R.string.visit_not_done).replace("\n", "");
            } else {
                if (date == null) {
                    return null;
                }

                if (visit_num == 1) {
                    return MessageFormat.format(" {0}: {1} \n {2}: {3} \n {4}: {5}",
                            context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date), "Vitamin A received", vit_a_mother, "IFA tablets received", ifa_mother);
                } else {
                    return MessageFormat.format("{0}: {1}", context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));
                }
            }
        }


        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(pnc_visit)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if (pnc_visit.equalsIgnoreCase("Yes")) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            } else if (pnc_visit.equalsIgnoreCase("No")) {
                return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            } else {
                return BaseAncHomeVisitAction.Status.PENDING;
            }
        }

        @Override
        public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
            Timber.d("onPayloadReceived");
        }
    }

}