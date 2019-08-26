package org.smartregister.chw.actionhelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import timber.log.Timber;

public class ANCCardAction extends HomeVisitActionHelper {
    private String anc_card;

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            anc_card = JsonFormUtils.getValue(jsonObject, "anc_card");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(anc_card))
            return null;

        return anc_card.equalsIgnoreCase("Yes") ? context.getString(R.string.yes) : context.getString(R.string.no);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(anc_card))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (anc_card.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (anc_card.equalsIgnoreCase("No")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }
}
