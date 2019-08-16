package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.util.JsonFormUtils.getValue;

public class ExclusiveBreastFeedingAction extends HomeVisitActionHelper {
    private Context context;
    private String exclusive_breast_feeding;
    private String serviceIteration;
    private String str_date;
    private Date parsedDate;
    private Date dob;

    public ExclusiveBreastFeedingAction(Context context, String serviceIteration, Date dob) {
        this.context = context;
        this.serviceIteration = serviceIteration;
        this.dob = dob;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        // prevent default behavoiur
    }

    @Override
    public String getPreProcessedSubTitle() {
        return MessageFormat.format("{0} {1}", context.getString(R.string.due), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dob));
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            exclusive_breast_feeding = getValue(jsonObject, "exclusive_breast_feeding");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(exclusive_breast_feeding))
            return "";

        return "No".equalsIgnoreCase(exclusive_breast_feeding) ? context.getString(R.string.yes) : context.getString(R.string.no);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(exclusive_breast_feeding))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (exclusive_breast_feeding.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else if (exclusive_breast_feeding.equalsIgnoreCase("No")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }
}