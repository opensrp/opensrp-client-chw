package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class TTAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
    private Context context;
    private String str_date;
    private Date parsedDate;
    private Triple<DateTime, VaccineRepo.Vaccine, String> vaccineStringTriple;

    public TTAction(Triple<DateTime, VaccineRepo.Vaccine, String> vaccineStringTriple, Context context) {
        this.vaccineStringTriple = vaccineStringTriple;
        this.context = context;
    }

    public JSONObject preProcess(JSONObject jsonObject, String iteration) throws JSONException {
        JSONArray fields = JsonFormUtils.fields(jsonObject);

        String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString("title");
        jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, iteration));

        JSONObject visit_field = JsonFormUtils.getFieldJSONObject(fields, "tt{0}_date");
        visit_field.put("key", MessageFormat.format(visit_field.getString("key"), iteration));
        visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), iteration));

        return jsonObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        Timber.v("onJsonFormLoaded");
    }

    @Override
    public String getPreProcessed() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            str_date = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("tt{0}_date", vaccineStringTriple.getRight()));

            try {
                parsedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(str_date);
            } catch (ParseException e) {
                parsedDate = null;
            }

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
        if (parsedDate != null) {
            return MessageFormat.format("{0} {1}",
                    context.getString(R.string.date_given),
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsedDate));
        }
        return context.getString(R.string.not_given);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(str_date))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (parsedDate != null) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }

    @Override
    public void onPayloadReceived(BaseAncHomeVisitAction ba) {
        try {
            JSONObject jsonObject = new JSONObject(ba.getJsonPayload());
            String value = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("tt{0}_date", vaccineStringTriple.getRight()));

            try {
                if (ba.getVaccineWrapper() != null && ba.getVaccineWrapper().size() > 0) {
                    DateTime updateDate = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(value);
                    ba.getVaccineWrapper().get(0).setUpdatedVaccineDate(updateDate, false);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}
