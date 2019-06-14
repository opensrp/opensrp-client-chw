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
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;
import org.smartregister.chw.util.ContactUtil;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.malaria.util.JsonFormUtils.fields;
import static org.smartregister.chw.util.JsonFormUtils.getValue;
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
        evaluateANCCounseling(actionList, memberObject, dateMap, context);
        evaluateSleepingUnderLLITN(view, actionList, context);
        evaluateANCCard(view, actionList, context);
        evaluateHealthFacilityVisit(actionList, memberObject, dateMap, context);
        evaluateImmunization(view, actionList, context);
        evaluateIPTP(view, actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));

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

    private void evaluateANCCounseling(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, MemberObject memberObject, Map<Integer, LocalDate> dateMap, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counseling), "", false, null,
                ANC_HOME_VISIT.ANC_COUNSELING);
        // open the form and inject the values
        try {
            if (StringUtils.isBlank(ba.getJsonPayload())) {

                JSONObject jsonObject = getJson(ba, memberObject);
                JSONArray fields = fields(jsonObject);

                int x = 1;
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<Integer, LocalDate> entry : dateMap.entrySet()) {
                    builder.append(MessageFormat.format("{0} {1} · {2} \n",
                            context.getString(R.string.counseling_visit),
                            memberObject.getConfirmedContacts() + x,
                            entry.getValue())
                    );
                    x++;
                }

                JSONObject visit_field = getFieldJSONObject(fields, "anc_counseling_toaster");
                visit_field.put("text", MessageFormat.format(visit_field.getString("text"), builder.toString()));

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

                        String value1 = getValue(jsonObject, "anc_counseling");
                        String value2 = getValue(jsonObject, "birth_hf_counseling");
                        String value3 = getValue(jsonObject, "nutrition_counseling");

                        if (value1.equalsIgnoreCase("Yes") && value2.equalsIgnoreCase("Yes") && value3.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (StringUtils.isNotBlank(value1) & StringUtils.isNotBlank(value2) & StringUtils.isNotBlank(value3)) {
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

        actionList.put(context.getString(R.string.anc_home_visit_counseling), ba);
    }

    private void evaluateSleepingUnderLLITN(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.SLEEPING_UNDER_LLITN, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "sleeping_llitn");

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

        actionList.put(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), ba);

    }

    private void evaluateANCCard(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_anc_card_received), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.ANC_CARD_RECEIVED, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_card");

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

        actionList.put(context.getString(R.string.anc_home_visit_anc_card_received), ba);
    }

    private void evaluateHealthFacilityVisit(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final MemberObject memberObject, Map<Integer, LocalDate> dateMap, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(visit, "", false, null, ANC_HOME_VISIT.HEALTH_FACILITY_VISIT);

        // open the form and inject the values
        try {
            if (StringUtils.isBlank(ba.getJsonPayload())) {

                JSONObject jsonObject = getJson(ba, memberObject);
                JSONArray fields = fields(jsonObject);

                if (dateMap.size() > 0) {
                    Map.Entry<Integer, LocalDate> entry = dateMap.entrySet().iterator().next();
                    LocalDate visitDate = entry.getValue();

                    ba.setTitle(MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1));

                    BaseAncHomeVisitAction.ScheduleStatus scheduleStatus = (visitDate.isBefore(LocalDate.now())) ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
                    String due = (visitDate.isBefore(LocalDate.now())) ? context.getString(R.string.overdue) : context.getString(R.string.due);

                    ba.setSubTitle(MessageFormat.format("{0} {1}", due, DateTimeFormat.forPattern("dd MMM yyyy").print(visitDate)));
                    ba.setScheduleStatus(scheduleStatus);

                    JSONObject visit_field = getFieldJSONObject(fields, "anc_hf_visit");
                    visit_field.put("label_info_title", MessageFormat.format(visit_field.getString("label_info_title"), memberObject.getConfirmedContacts() + 1));
                    visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), memberObject.getConfirmedContacts() + 1, visitDate));

                    // current visit count
                    getFieldJSONObject(fields, "confirmed_visits").put(JsonFormConstants.VALUE, memberObject.getConfirmedContacts());
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
                    Timber.e(e);
                }
            }
        });

        actionList.put(visit, ba);
    }

    private void evaluateImmunization(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String immunization = MessageFormat.format(context.getString(R.string.anc_home_visit_tt_immunization), 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(immunization, "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.TT_IMMUNIZATION, null),
                null);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("tt{0}_date", 1));

                        try {
                            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(immunization, ba);
    }

    private void evaluateIPTP(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String iptp = MessageFormat.format(context.getString(R.string.anc_home_visit_iptp_sp), 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(iptp, "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.IPTP_SP, null),
                null);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("iptp{0}_date", 1));

                        try {
                            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(iptp, ba);
    }

}
