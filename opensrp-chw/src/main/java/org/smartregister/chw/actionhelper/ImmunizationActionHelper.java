package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.Util;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class ImmunizationActionHelper implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {

    private Context context;
    private List<VaccineWrapper> wrappers;
    private LocalDate dueDate;
    private AlertStatus status;

    private List<String> keys = new ArrayList<>();
    private Map<String, List<String>> completedVaccines = new HashMap<>();
    private List<String> notDoneVaccines = new ArrayList<>();

    public ImmunizationActionHelper(Context context, List<VaccineWrapper> wrappers) {
        this.context = context;
        this.wrappers = wrappers;

        initialize();
    }

    private void initialize() {
        LocalDate dueDate = null;
        AlertStatus myStatus = null;

        for (VaccineWrapper vaccineWrapper : wrappers) {
            Alert alert = vaccineWrapper.getAlert();

            if (myStatus == null || (alert != null && !alert.status().equals(AlertStatus.expired))) {
                myStatus = alert.status();
            } else if (alert != null && alert.status().equals(AlertStatus.urgent)) {
                myStatus = alert.status();
            }

            if (dueDate == null) {
                dueDate = new LocalDate(alert.startDate());
            }

            keys.add(Util.removeSpaces(vaccineWrapper.getName()));
        }

        this.dueDate = new LocalDate(dueDate);
        this.status = myStatus;
    }

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
            notDoneVaccines.clear();
            completedVaccines.clear();

            // key / name pair
            for (String key : keys) {
                String val = JsonFormUtils.getValue(jsonObject, key);
                if (val.equalsIgnoreCase(Constants.HOME_VISIT.VACCINE_NOT_GIVEN)) {
                    notDoneVaccines.add(key);
                } else {
                    List<String> vacs = completedVaccines.get(val);
                    if (vacs == null)
                        vacs = new ArrayList<>();

                    vacs.add(key);

                    completedVaccines.put(val, vacs);
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        if (status.value().equals(AlertStatus.urgent.value()))
            return BaseAncHomeVisitAction.ScheduleStatus.OVERDUE;

        return BaseAncHomeVisitAction.ScheduleStatus.DUE;
    }

    @Override
    public String getPreProcessedSubTitle() {
        String due = (status.name().equals(AlertStatus.urgent.name()) ? context.getString(R.string.overdue) : context.getString(R.string.due));
        return MessageFormat.format("{0} {1}", due, DateTimeFormat.forPattern("dd MMM yyyy").print(dueDate));
    }

    /**
     * update all the vaccine wrappers with the dates required
     *
     * @param s
     * @return
     */
    @Override
    public String postProcess(String s) {
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        SimpleDateFormat native_date = new SimpleDateFormat(Constants.DATE_FORMATS.NATIVE_FORMS, Locale.getDefault());
        SimpleDateFormat new_date = new SimpleDateFormat(org.smartregister.chw.util.Constants.DATE_FORMATS.HOME_VISIT_DISPLAY, Locale.getDefault());

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : completedVaccines.entrySet()) {
            StringBuilder completedBuilder = new StringBuilder();
            for (String vac : entry.getValue()) {
                completedBuilder.append(completedBuilder.length() > 0 ? completedBuilder.append(", ") : vac);
            }

            if (completedBuilder.length() > 0) {
                try {
                    if (builder.length() > 0)
                        builder.append(" · ");

                    builder.append(MessageFormat.format("{0} {1} {2}",
                            completedBuilder.toString(),
                            context.getString(R.string.given_on_with_spaces),
                            new_date.format(native_date.parse(entry.getKey()))
                    ));
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }
        }

        StringBuilder pendingBuilder = new StringBuilder();
        for (String s : notDoneVaccines) {
            pendingBuilder.append(pendingBuilder.length() > 0 ? pendingBuilder.append(", ").append(s) : s);
        }

        if (pendingBuilder.length() > 0) {

            if (builder.length() > 0)
                builder.append(" · ");

            builder.append(MessageFormat.format("{0} {1}",
                    pendingBuilder.toString(),
                    context.getString(R.string.not_given_with_spaces)
            ));
        }

        return builder.toString();
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
