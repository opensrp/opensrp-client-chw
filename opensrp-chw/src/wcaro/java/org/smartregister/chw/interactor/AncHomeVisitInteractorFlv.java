package org.smartregister.chw.interactor;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
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
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;
import org.smartregister.chw.util.ContactUtil;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.service.AlertService;

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

import static org.smartregister.chw.malaria.util.JsonFormUtils.fields;
import static org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue;
import static org.smartregister.chw.util.JsonFormUtils.getValue;
import static org.smartregister.chw.util.RecurringServiceUtil.getRecurringServices;
import static org.smartregister.chw.util.Utils.getDayOfMonthSuffix;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        // get contact schedule
        Map<Integer, LocalDate> dateMap = getContactSchedule(memberObject);

        // get vaccine schedule if ga > 13
        VaccineTaskModel vaccineTaskModel = null;

        DateTime lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(memberObject.getLastMenstrualPeriod());
        int ga = Days.daysBetween(lastMenstrualPeriod, new DateTime()).getDays() / 7;

        if (ga >= 13) {
            vaccineTaskModel = getWomanVaccine(memberObject.getBaseEntityId(), lastMenstrualPeriod, getNotGivenVaccines());
        }

        evaluateDangerSigns(actionList, context);
        evaluateANCCounseling(actionList, memberObject, dateMap, context);
        evaluateSleepingUnderLLITN(view, actionList, context);
        evaluateANCCard(view, actionList, context);
        evaluateHealthFacilityVisit(actionList, memberObject, dateMap, context);
        evaluateTTImmunization(view, actionList, memberObject, vaccineTaskModel, context);
        evaluateIPTP(view, actionList, memberObject, context);
        evaluateObservation(actionList, context);

        return actionList;
    }

    private Map<Integer, LocalDate> getContactSchedule(MemberObject memberObject) {
        // get contact

        LocalDate lastContact = new DateTime(memberObject.getDateCreated()).toLocalDate();
        boolean isFirst = (StringUtils.isBlank(memberObject.getLastContactVisit()));
        LocalDate lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastMenstrualPeriod());

        if (StringUtils.isNotBlank(memberObject.getLastContactVisit()))
            lastContact = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastContactVisit());

        Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();

        // today is the due date for the very first visit
        if (isFirst) {
            dateMap.put(0, LocalDate.now());
        }

        dateMap.putAll(ContactUtil.getContactWeeks(isFirst, lastContact, lastMenstrualPeriod));

        return dateMap;
    }

    private JSONObject getJson(BaseAncHomeVisitAction ba, String baseEntityID) throws Exception {
        return getJson(ba.getFormName(), baseEntityID);
    }

    private JSONObject getJson(String formName, String baseEntityID) throws Exception {
        String locationId = ChwApplication.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
        JSONObject jsonObject = JsonFormUtils.getFormAsJson(formName);
        JsonFormUtils.getRegistrationForm(jsonObject, baseEntityID, locationId);
        return jsonObject;
    }

    private void evaluateDangerSigns(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_danger_signs), "", false, null,
                ANC_HOME_VISIT.getDangerSigns());
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                ba.setSubTitle("");
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "danger_signs_counseling");
                        ba.setSubTitle(getDangerSignsText(jsonObject, context));

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            ba.setSubTitle("");
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

    private String getDangerSignsText(JSONObject jsonObject, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        String signs_present = getCheckBoxValue(jsonObject, "danger_signs_present");
        String counseling = getValue(jsonObject, "danger_signs_counseling");

        stringBuilder.append(MessageFormat.format("Danger signs: {0}", signs_present));
        stringBuilder.append("\n");
        stringBuilder.append(MessageFormat.format("Danger signs counseling {0}",
                (counseling.equalsIgnoreCase("Yes") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase())
        ));
        return stringBuilder.toString();
    }

    private void evaluateANCCounseling(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, MemberObject memberObject, Map<Integer, LocalDate> dateMap, final Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counseling), "", false, null,
                ANC_HOME_VISIT.getAncCounseling());
        // open the form and inject the values
        try {
            if (StringUtils.isBlank(ba.getJsonPayload())) {

                JSONObject jsonObject = getJson(ba, memberObject.getBaseEntityId());
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

                ba.setProcessedJsonPayload(jsonObject.toString());
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

                        ba.setSubTitle(getANCCounselingText(jsonObject, context));

                        if (value1.equalsIgnoreCase("Yes") && value2.equalsIgnoreCase("Yes") && value3.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (StringUtils.isNotBlank(value1) & StringUtils.isNotBlank(value2) & StringUtils.isNotBlank(value3)) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            ba.setSubTitle("");
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

    private String getANCCounselingText(JSONObject jsonObject, Context context) {

        List<String> yes = new ArrayList<>();
        List<String> nos = new ArrayList<>();
        if (getValue(jsonObject, "anc_counseling").equals("Yes")) {
            yes.add("ANC visit counseling");
        } else {
            nos.add("ANC visit counseling");
        }

        if (getValue(jsonObject, "birth_hf_counseling").equals("Yes")) {
            yes.add("Delivery at health facility counseling");
        } else {
            nos.add("Delivery at health facility counseling");
        }

        if (getValue(jsonObject, "nutrition_counseling").equals("Yes")) {
            yes.add("Nutrition counseling");
        } else {
            nos.add("Nutrition counseling");
        }
        StringBuilder stringBuilder = new StringBuilder();

        if (yes.size() > 0) {
            for (String s : yes) {
                stringBuilder.append(s).append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append(context.getString(R.string.done).toLowerCase());
        }

        if (nos.size() > 0) {
            if (stringBuilder.toString().trim().length() > 0) {
                stringBuilder.append(" · ");
            }

            for (String s : nos) {
                stringBuilder.append(s).append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append(context.getString(R.string.not_done).toLowerCase());
        }

        return stringBuilder.toString();
    }

    private void evaluateSleepingUnderLLITN(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.getSleepingUnderLlitn(), null, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "sleeping_llitn");

                        ba.setSubTitle(StringUtils.capitalize(value.trim().toLowerCase()));

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

        actionList.put(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), ba);

    }

    private void evaluateANCCard(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_anc_card_received), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.getAncCardReceived(), null, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_card");
                        ba.setSubTitle(StringUtils.capitalize(value.trim()));

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

        actionList.put(context.getString(R.string.anc_home_visit_anc_card_received), ba);
    }

    private void evaluateHealthFacilityVisit(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final MemberObject memberObject, Map<Integer, LocalDate> dateMap, final Context context) throws BaseAncHomeVisitAction.ValidationException {

        String visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(visit, "", false, null, ANC_HOME_VISIT.getHealthFacilityVisit());

        evaluateHealthFacilityVisitPreExec(ba, memberObject, dateMap, context);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_hf_visit");

                        ba.setSubTitle(getHealthFacilityVisitText(jsonObject, context));
                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            ba.setSubTitle("");
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
                        ba.setProcessedJsonPayload(jsonObject.toString());
                    }

                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });

        actionList.put(visit, ba);
    }

    private void evaluateHealthFacilityVisitPreExec(BaseAncHomeVisitAction ba, final MemberObject memberObject, Map<Integer, LocalDate> dateMap, final Context context) {
        // open the form and inject the values
        try {
            if (StringUtils.isBlank(ba.getJsonPayload())) {

                JSONObject jsonObject = getJson(ba, memberObject.getBaseEntityId());
                JSONArray fields = fields(jsonObject);

                if (dateMap.size() > 0) {
                    Map.Entry<Integer, LocalDate> entry = dateMap.entrySet().iterator().next();
                    LocalDate visitDate = entry.getValue();

                    ba.setTitle(MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), memberObject.getConfirmedContacts() + 1));

                    BaseAncHomeVisitAction.ScheduleStatus scheduleStatus = (visitDate.isBefore(LocalDate.now())) ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
                    String due = (visitDate.isBefore(LocalDate.now())) ? context.getString(R.string.overdue) : context.getString(R.string.due);

                    ba.setSubTitle(MessageFormat.format("{0} {1}", due, DateTimeFormat.forPattern("dd MMM yyyy").print(visitDate)));
                    ba.setScheduleStatus(scheduleStatus);

                    String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString("title");
                    jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, memberObject.getConfirmedContacts() + 1));

                    JSONObject visit_field = getFieldJSONObject(fields, "anc_hf_visit");
                    visit_field.put("label_info_title", MessageFormat.format(visit_field.getString("label_info_title"), memberObject.getConfirmedContacts() + 1));
                    visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), memberObject.getConfirmedContacts() + 1, visitDate));

                    // current visit count
                    getFieldJSONObject(fields, "confirmed_visits").put(JsonFormConstants.VALUE, memberObject.getConfirmedContacts());
                }

                ba.setProcessedJsonPayload(jsonObject.toString());
                ba.setActionStatus(BaseAncHomeVisitAction.Status.PENDING);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getHealthFacilityVisitText(JSONObject jsonObject, Context context) throws ParseException {

        String value = getValue(jsonObject, "anc_hf_visit");
        StringBuilder stringBuilder = new StringBuilder();
        if (value.equalsIgnoreCase("No")) {
            stringBuilder.append(context.getString(R.string.visit_not_done).replace("\n", ""));
        } else {

            String date_str = getValue(jsonObject, "anc_hf_visit_date");
            String weight = getValue(jsonObject, "weight");
            String bp = getValue(jsonObject, "sys_bp");
            String dia_bp = getValue(jsonObject, "dia_bp");
            String hb = getValue(jsonObject, "hb_level");
            String ifa = getValue(jsonObject, "ifa_received");
            String testsDone = getCheckBoxValue(jsonObject, "tests_done");

            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date_str);

            stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)));
            stringBuilder.append(MessageFormat.format("{0}: {1} {2}\n", context.getString(R.string.weight), weight, context.getString(R.string.kg)));
            stringBuilder.append(MessageFormat.format("{0}: {1}/{2} {3}\n", context.getString(R.string.str_bp), bp, dia_bp, context.getString(R.string.mmHg)));
            stringBuilder.append(MessageFormat.format("{0}: {1} {2}\n", context.getString(R.string.hb_level), hb, context.getString(R.string.gdl)));
            stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.ifa_received), ifa));
            stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.tests_done), testsDone));
        }

        return stringBuilder.toString();
    }

    private void evaluateTTImmunization(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final MemberObject memberObject, VaccineTaskModel vaccineTaskModel, final Context context) throws BaseAncHomeVisitAction.ValidationException {

        // if there are no pending vaccines
        if (vaccineTaskModel == null || vaccineTaskModel.getScheduleList().size() < 1) {
            return;
        }
        // compute the due date
        final Triple<DateTime, VaccineRepo.Vaccine, String> details = getIndividualVaccine(vaccineTaskModel, "TT");
        if (details == null || details.getLeft().isAfter(new DateTime())) {
            return;
        }

        String immunization = MessageFormat.format(context.getString(R.string.anc_home_visit_tt_immunization), details.getRight());
        /*
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(immunization, "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.getTtImmunization(), null, details.getRight()),
                null);
                */
        final BaseAncHomeVisitAction ba = evaluateTTImmunizationPreExec(view, immunization, ANC_HOME_VISIT.getTtImmunization(), memberObject, details.getRight());

        ba.setVaccineWrapper(getVaccineWrapper(details.getMiddle(), vaccineTaskModel));

        int overdueMonth = new Period(details.getLeft(), new DateTime()).getMonths();
        String dueState;
        if (overdueMonth < 1) {
            dueState = context.getString(R.string.due);
            ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.DUE);
        } else {
            dueState = context.getString(R.string.overdue);
            ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.OVERDUE);
        }

        ba.setSubTitle(MessageFormat.format("{0} {1}", dueState, DateTimeFormat.forPattern("dd MMM yyyy").print(new DateTime(details.getLeft()))));
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("tt{0}_date", details.getRight()));

                        ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.DUE);
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);

                            String subText = MessageFormat.format("{0} {1}",
                                    context.getString(R.string.date_given),
                                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));

                            ba.setSubTitle(subText);
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            ba.setSubTitle(context.getString(R.string.not_given));
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        // update vaccine date
        ba.setOnPayLoadReceived(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject jsonObject = new JSONObject(ba.getJsonPayload());
                    String value = getValue(jsonObject, MessageFormat.format("tt{0}_date", details.getRight()));

                    try {
                        if (ba.getVaccineWrapper() != null) {
                            DateTime updateDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(value);
                            ba.getVaccineWrapper().setUpdatedVaccineDate(updateDate, false);
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }

                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });

        // don't show if its after now
        if (!details.getLeft().isAfterNow()) {
            actionList.put(immunization, ba);
        }
    }

    private BaseAncHomeVisitAction evaluateTTImmunizationPreExec(BaseAncHomeVisitContract.View view, String curTitle, String formName, final MemberObject memberObject, String iteration) throws BaseAncHomeVisitAction.ValidationException {
        // open the form and inject the values

        try {
            JSONObject jsonObject = getJson(formName, memberObject.getBaseEntityId());
            JSONArray fields = fields(jsonObject);

            String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString("title");
            String formatted_count = MessageFormat.format("{0}{1}", iteration, getDayOfMonthSuffix(iteration));
            jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, formatted_count));

            JSONObject visit_field = getFieldJSONObject(fields, "tt{0}_date");
            visit_field.put("key", MessageFormat.format(visit_field.getString("key"), iteration));
            visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), iteration));

            return new BaseAncHomeVisitAction(curTitle, "", false,
                    BaseAncHomeVisitFragment.getInstance(view, null, jsonObject, iteration),
                    null);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private void evaluateIPTP(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, MemberObject memberObject, final Context context) throws BaseAncHomeVisitAction.ValidationException {
        // if there are no pending vaccines

        DateTime lmp = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(memberObject.getLastMenstrualPeriod());
        Map<String, ServiceWrapper> serviceWrapperMap = getRecurringServices(memberObject.getBaseEntityId(), lmp);
        ServiceWrapper serviceWrapper = serviceWrapperMap.get("IPTp-SP");

        if (serviceWrapper == null) {
            return;
        }

        final String serviceIteration = serviceWrapper.getName().substring(serviceWrapper.getName().length() - 1);

        String iptp = MessageFormat.format(context.getString(R.string.anc_home_visit_iptp_sp), serviceIteration);
        final BaseAncHomeVisitAction ba = evaluateIPTPPreExec(view, iptp, ANC_HOME_VISIT.getIptpSp(), memberObject, serviceIteration);
        ba.setServiceWrapper(serviceWrapper);

        String dueState;
        int overdueMonth = new Period(serviceWrapper.getVaccineDate(), new DateTime()).getMonths();
        if (overdueMonth < 1) {
            dueState = context.getString(R.string.due);
            ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.DUE);
        } else {
            dueState = context.getString(R.string.overdue);
            ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.OVERDUE);
        }

        ba.setSubTitle(MessageFormat.format("{0} {1}", dueState, DateTimeFormat.forPattern("dd MMM yyyy").print(new DateTime(serviceWrapper.getVaccineDate()))));
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("iptp{0}_date", serviceIteration));

                        ba.setScheduleStatus(BaseAncHomeVisitAction.ScheduleStatus.DUE);
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);


                            String subText = MessageFormat.format("{0} {1}",
                                    context.getString(R.string.date_given),
                                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));

                            ba.setSubTitle(subText);

                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            ba.setSubTitle(context.getString(R.string.not_given));
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        // update vaccine date
        ba.setOnPayLoadReceived(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject jsonObject = new JSONObject(ba.getJsonPayload());
                    String value = getValue(jsonObject, MessageFormat.format("iptp{0}_date", serviceIteration));

                    try {
                        if (ba.getServiceWrapper() != null) {
                            DateTime updateDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(value);
                            ba.getServiceWrapper().setUpdatedVaccineDate(updateDate, false);
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }

                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });

        // don't show if its after now
        if (!serviceWrapper.getVaccineDate().isAfterNow()) {
            actionList.put(iptp, ba);
        }
    }

    private BaseAncHomeVisitAction evaluateIPTPPreExec(BaseAncHomeVisitContract.View view, String curTitle, String formName, final MemberObject memberObject, String iteration) throws BaseAncHomeVisitAction.ValidationException {
        // open the form and inject the values

        try {
            JSONObject jsonObject = getJson(formName, memberObject.getBaseEntityId());
            JSONArray fields = fields(jsonObject);

            String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString("title");
            String formatted_count = MessageFormat.format("{0}{1}", iteration, getDayOfMonthSuffix(iteration));
            jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, formatted_count));

            JSONObject visit_field = getFieldJSONObject(fields, "iptp{0}_date");
            visit_field.put("key", MessageFormat.format(visit_field.getString("key"), iteration));
            visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), iteration));

            return new BaseAncHomeVisitAction(curTitle, "", false,
                    BaseAncHomeVisitFragment.getInstance(view, null, jsonObject, iteration),
                    null);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private void evaluateObservation(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, final Context context) throws BaseAncHomeVisitAction.ValidationException {

        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.getObservationAndIllness());
        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), ba);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                ba.setSubTitle("");
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        LocalDate illnessDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(getValue(jsonObject, "date_of_illness"));
                        String desc = getValue(jsonObject, "illness_description");
                        String action = getValue(jsonObject, "action_taken");

                        String builder = MessageFormat.format("{0}: {1}\n {2}: {3}",
                                DateTimeFormat.forPattern("dd MMM yyyy").print(illnessDate),
                                desc, context.getString(R.string.action_taken), action
                        );
                        ba.setSubTitle(builder);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                } else {
                    ba.setSubTitle("");
                }
                return ba.computedStatus();
            }
        });
    }

    private VaccineTaskModel getWomanVaccine(String baseEntityID, DateTime lmpDate, List<VaccineWrapper> notDoneVaccines) {
        AlertService alertService = ChwApplication.getInstance().getContext().alertService();
        VaccineRepository vaccineRepository = ChwApplication.getInstance().vaccineRepository();

        // get offline alerts
        VaccineSchedule.updateOfflineAlerts(baseEntityID, lmpDate, "woman");
        ChwServiceSchedule.updateOfflineAlerts(baseEntityID, lmpDate); // get services

        //
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(baseEntityID, VaccinateActionUtils.allAlertNames("woman"));
        List<Vaccine> vaccines = vaccineRepository.findByEntityId(baseEntityID);
        Map<String, Date> receivedVaccines = receivedVaccines(vaccines);

        if (notDoneVaccines != null) {
            for (int i = 0; i < notDoneVaccines.size(); i++) {
                receivedVaccines.put(notDoneVaccines.get(i).getName().toLowerCase(), new Date());
            }
        }

        List<Map<String, Object>> sch = generateScheduleList("woman", lmpDate, receivedVaccines, alerts);
        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
        vaccineTaskModel.setAlerts(alerts);
        vaccineTaskModel.setVaccines(vaccines);
        vaccineTaskModel.setReceivedVaccines(receivedVaccines);
        vaccineTaskModel.setScheduleList(sch);

        return vaccineTaskModel;
    }

    // vaccine utils
    private Triple<DateTime, VaccineRepo.Vaccine, String> getIndividualVaccine(VaccineTaskModel vaccineTaskModel, String type) {
        // compute the due date
        Map<String, Object> map = null;
        for (Map<String, Object> mapVac : vaccineTaskModel.getScheduleList()) {
            VaccineRepo.Vaccine myVac = (VaccineRepo.Vaccine) mapVac.get("vaccine");
            String status = (String) mapVac.get("status");
            if (myVac != null && myVac.display().toLowerCase().contains(type.toLowerCase()) && status != null && status.equals("due")) {
                map = mapVac;
                break;
            }
        }

        if (map == null) {
            return null;
        }

        DateTime date = (DateTime) map.get("date");
        VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) map.get("vaccine");
        if (vaccine == null || date == null) {
            return null;
        }
        String vc_count = vaccine.name().substring(vaccine.name().length() - 1);

        return Triple.of(date, vaccine, vc_count);
    }

    // read vaccine repo for all not given vaccines
    private List<VaccineWrapper> getNotGivenVaccines() {
        return new ArrayList<>();
    }

    private VaccineWrapper getVaccineWrapper(VaccineRepo.Vaccine vaccine, VaccineTaskModel vaccineTaskModel) {
        VaccineWrapper vaccineWrapper = new VaccineWrapper();
        vaccineWrapper.setVaccine(vaccine);
        vaccineWrapper.setName(vaccine.display());
        vaccineWrapper.setDbKey(getVaccineId(vaccine.display(), vaccineTaskModel));
        vaccineWrapper.setDefaultName(vaccine.display());
        return vaccineWrapper;
    }

    private Long getVaccineId(String vaccineName, VaccineTaskModel vaccineTaskModel) {
        for (Vaccine vaccine : vaccineTaskModel.getVaccines()) {
            if (vaccine.getName().equalsIgnoreCase(vaccineName)) {
                return vaccine.getId();
            }
        }
        return null;
    }

}
