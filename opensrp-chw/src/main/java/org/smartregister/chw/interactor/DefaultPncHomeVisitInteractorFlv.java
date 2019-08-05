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
import org.smartregister.chw.actionhelper.DangerSignsAction;
import org.smartregister.chw.actionhelper.ObservationAction;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.util.JsonFormUtils.getValue;
import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public abstract class DefaultPncHomeVisitInteractorFlv implements PncHomeVisitInteractor.Flavor {

    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;
    protected Context context;
    protected Map<String, List<VisitDetail>> details = null;
    protected List<Person> children;
    protected MemberObject memberObject;
    protected BaseAncHomeVisitContract.View view;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        this.view = view;
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
                        visit_num = 2;
                        break;
                    case "42":
                        visit_num = 3;
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
                            .withHelper(new PNCHealthFacilityVisitHelper(visitRule, visit_num))
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
        HomeVisitActionHelper kangarooHelper = new HomeVisitActionHelper() {
            private String kangaroo;

            @Override
            public void onPayloadReceived(String jsonPayload) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    kangaroo = JsonFormUtils.getValue(jsonObject, "kangaroo");
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }

            @Override
            public String evaluateSubTitle() {
                if (StringUtils.isBlank(kangaroo))
                    return null;

                return kangaroo.equalsIgnoreCase("Yes") ? context.getString(R.string.yes) : context.getString(R.string.no);
            }

            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isBlank(kangaroo))
                    return BaseAncHomeVisitAction.Status.PENDING;

                if (kangaroo.equalsIgnoreCase("Yes")) {
                    return BaseAncHomeVisitAction.Status.COMPLETED;
                } else if (kangaroo.equalsIgnoreCase("No")) {
                    return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                } else {
                    return BaseAncHomeVisitAction.Status.PENDING;
                }
            }
        };

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.pnc_kangeroo_mother_care))
                .withOptional(false)
                .withDetails(details)
                .withDestinationFragment(BaseAncHomeVisitFragment.getInstance(view, Constants.JSON_FORM.PNC_HOME_VISIT.getKangarooCare(), null, details, null))
                .withHelper(kangarooHelper)
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
        private int visit_num;

        private String pnc_visit;
        private String pnc_hf_visit_date;
        private String baby_weight;
        private String baby_temp;

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
                JSONArray fields = fields(jsonObject);


                String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString(JsonFormConstants.STEP_TITLE);
                jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, visitRule.getVisitName()));

                JSONObject pnc_visit = getFieldJSONObject(fields, "pnc_visit_{0}");
                pnc_visit.put(JsonFormConstants.KEY, MessageFormat.format("pnc_visit_{0}", visit_num));
                pnc_visit.put("hint",
                        MessageFormat.format(pnc_visit.getString(JsonFormConstants.HINT),
                                visitRule.getVisitName(),
                                DateTimeFormat.forPattern("dd MMM yyyy").print(visitRule.getDueDate()
                                )
                        )
                );

                JSONObject pnc_visit_date = getFieldJSONObject(fields, "pnc_hf_visit{0}_date");
                pnc_visit_date.put(JsonFormConstants.KEY, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));
                pnc_visit_date.put("hint",
                        MessageFormat.format(pnc_visit_date.getString(JsonFormConstants.HINT), visitRule.getVisitName())
                );
                updateObjectRelevance(pnc_visit_date);
                updateObjectRelevance(getFieldJSONObject(fields, "baby_weight"));
                updateObjectRelevance(getFieldJSONObject(fields, "baby_temp"));

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
                pnc_visit = getValue(jsonObject, MessageFormat.format("pnc_visit_{0}", visit_num));
                pnc_hf_visit_date = getValue(jsonObject, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));
                baby_weight = getValue(jsonObject, "baby_weight");
                baby_temp = getValue(jsonObject, "baby_temp");

                if (StringUtils.isNotBlank(pnc_hf_visit_date))
                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(pnc_hf_visit_date);
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

                JSONArray field = fields(jsonObject);
                JSONObject confirmed_visits = getFieldJSONObject(field, "confirmed_health_facility_visits");
                JSONObject facility_visit_date = getFieldJSONObject(field, "last_health_facility_visit_date");
                pnc_hf_visit_date = getValue(jsonObject, MessageFormat.format("pnc_hf_visit{0}_date", visit_num));

                String count = String.valueOf(visit_num);
                String value = getValue(jsonObject, MessageFormat.format("pnc_visit_{0}", visit_num));
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
            if (StringUtils.isBlank(pnc_visit))
                return null;

            return MessageFormat.format("{0} : {1}", context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));
        }


        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(pnc_visit))
                return BaseAncHomeVisitAction.Status.PENDING;

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
