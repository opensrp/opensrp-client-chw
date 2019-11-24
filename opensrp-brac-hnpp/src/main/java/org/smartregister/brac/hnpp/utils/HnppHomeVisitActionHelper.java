package org.smartregister.brac.hnpp.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.text.MessageFormat;

import timber.log.Timber;

public class HnppHomeVisitActionHelper extends HomeVisitActionHelper {
    private String signs_present = "";
    private String counseling = "Yes";

    @Override
    public void onPayloadReceived(String jsonPayload) {

    }

    @Override
    public String evaluateSubTitle() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageFormat.format("{0}: {1}", context.getString(R.string.anc_home_visit_danger_signs), signs_present));
        stringBuilder.append("\n");
        stringBuilder.append(MessageFormat.format("{0} {1}",
                context.getString(R.string.danger_signs_counselling),
                (counseling.equalsIgnoreCase("Yes") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase())
        ));

        if (counseling.equalsIgnoreCase("Yes") || counseling.equalsIgnoreCase("No"))
            return stringBuilder.toString();

        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(counseling))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (counseling.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (counseling.equalsIgnoreCase("No")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }

}
