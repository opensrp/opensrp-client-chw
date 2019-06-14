package org.smartregister.chw.interactor;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;
import org.smartregister.chw.util.ContactUtil;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.util.JsonFormUtils.getValue;
import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {
    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        // get contact
        LocalDate lastContact = new DateTime(memberObject.getDateCreated()).toLocalDate();
        boolean isFirst = (StringUtils.isNotBlank(memberObject.getLastContactVisit()));
        LocalDate lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastMenstrualPeriod());

        if (StringUtils.isNotBlank(memberObject.getLastContactVisit()))
            lastContact = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastContactVisit());

        Map<Integer, LocalDate> dateMap = ContactUtil.getContactWeeks(isFirst, lastContact, lastMenstrualPeriod);

        evaluateDangerSigns(actionList, context);
        evaluateHealthFacilityVisit(actionList, memberObject, dateMap, context);
        evaluateFamilyPlanning(actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_nutrition_status), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_nutrition_status), "", false, null,
                ANC_HOME_VISIT.NUTRITION_STATUS));
        actionList.put(context.getString(R.string.anc_home_visit_counselling_task), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counselling_task), "", false, null,
                ANC_HOME_VISIT.COUNSELLING));

        evaluateMalaria(actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));
        actionList.put(context.getString(R.string.anc_home_visit_remarks_and_comments), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_remarks_and_comments), "", true, null,
                ANC_HOME_VISIT.REMARKS_AND_COMMENTS));

        return actionList;
    }

    private JSONObject getJson(BaseAncHomeVisitAction ba, MemberObject memberObject) throws Exception {
        String locationId = ChwApplication.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
        JSONObject jsonObject = JsonFormUtils.getFormAsJson(ba.getFormName());
        JsonFormUtils.getRegistrationForm(jsonObject, memberObject.getBaseEntityId(), locationId);
        return jsonObject;
    }

    private void evaluateDangerSigns(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_danger_signs), "", false, null,
                ANC_HOME_VISIT.DANGER_SIGNS);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "danger_signs_counseling");

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PENDING;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_danger_signs), ba);
    }

    private void evaluateHealthFacilityVisit(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final MemberObject memberObject, Map<Integer, LocalDate> dateMap, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(visit, "", false, null, ANC_HOME_VISIT.HEALTH_FACILITY_VISIT);

        // open the form and inject the values
        try {
            if (StringUtils.isBlank(ba.getJsonPayload())) {

                JSONObject jsonObject = getJson(ba, memberObject);
                JSONArray fields = fields(jsonObject);

                JSONArray field = fields(jsonObject);

                if (dateMap.size() > 0) {
                    Map.Entry<Integer, LocalDate> entry = dateMap.entrySet().iterator().next();
                    LocalDate visitDate = entry.getValue();

                    ba.setTitle(MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1));

                    // update form
                    if (visitDate.isBefore(LocalDate.now())) {
                        ba.setSubTitle(MessageFormat.format("{0} {1}", context.getString(R.string.overdue), DateTimeFormat.forPattern("dd MMM yyyy").print(visitDate)));
                        ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.OVERDUE);
                    } else {
                        ba.setSubTitle(MessageFormat.format("{0} {1}", context.getString(R.string.due), DateTimeFormat.forPattern("dd MMM yyyy").print(visitDate)));
                        ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.DUE);
                    }

                    JSONObject visit_field = getFieldJSONObject(field, "anc_hf_visit");
                    visit_field.put("label_info_title", MessageFormat.format(visit_field.getString("label_info_title"), memberObject.getConfirmedContacts() + 1));
                    visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), memberObject.getConfirmedContacts() + 1, visitDate));

                    // current visit count
                    JSONObject confirmed_visits = getFieldJSONObject(field, "confirmed_visits");
                    confirmed_visits.put(JsonFormConstants.VALUE, memberObject.getConfirmedContacts());
                }

                ba.setJsonPayload(jsonObject.toString());
                ba.setActionStatus(BaseAncHomeVisitAction.Status.PENDING);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_hf_visit");

                        if (value.equalsIgnoreCase("Yes")) {

                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PENDING;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        ba.setOnPayLoadReceived(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                    JSONArray field = fields(jsonObject);
                    JSONObject confirmed_visits = getFieldJSONObject(field, "confirmed_visits");

                    String count = String.valueOf(memberObject.getConfirmedContacts());
                    String value = getValue(jsonObject, "anc_hf_visit");
                    if (value.equalsIgnoreCase("Yes")) {
                        count = String.valueOf(memberObject.getConfirmedContacts() + 1);
                    }

                    if (!confirmed_visits.getString(JsonFormConstants.VALUE).equals(count)) {
                        confirmed_visits.put(JsonFormConstants.VALUE, memberObject.getConfirmedContacts() + 1);
                        ba.setJsonPayload(jsonObject.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        actionList.put(visit, ba);
    }

    private void evaluateFamilyPlanning(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_family_planning), "", false, null,
                ANC_HOME_VISIT.FAMILY_PLANNING);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "fam_planning");

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_family_planning), ba);
    }

    private void evaluateMalaria(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_malaria_prevention), "", false, null,
                ANC_HOME_VISIT.MALARIA);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value1 = getValue(jsonObject, "fam_llin");
                        String value2 = getValue(jsonObject, "llin_2days");
                        String value3 = getValue(jsonObject, "llin_condition");

                        if (value1.equalsIgnoreCase("Yes") && value2.equalsIgnoreCase("Yes") && value3.equalsIgnoreCase("Okay")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_malaria_prevention), ba);
    }
}
