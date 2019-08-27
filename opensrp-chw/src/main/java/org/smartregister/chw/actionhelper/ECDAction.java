package org.smartregister.chw.actionhelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.text.MessageFormat;

import timber.log.Timber;

public class ECDAction extends HomeVisitActionHelper {
    private String develop_warning_signs;
    private String stim_skills;
    private String early_learning;

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            develop_warning_signs = JsonFormUtils.getValue(jsonObject, "develop_warning_signs");
            stim_skills = JsonFormUtils.getValue(jsonObject, "stim_skills");
            early_learning = JsonFormUtils.getValue(jsonObject, "early_learning");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(develop_warning_signs))
            return null;

        StringBuilder builder = new StringBuilder();
        builder.append(MessageFormat.format("{0} {1}\n",
                context.getString(R.string.dev_warning_sign),
                getTranslatedValue(develop_warning_signs)
        ));
        builder.append(MessageFormat.format("{0} {1}",
                context.getString(R.string.care_stim_skill),
                getTranslatedValue(stim_skills)
        ));

        if (StringUtils.isNotBlank(early_learning))
            builder.append(MessageFormat.format("\n{0} {1}",
                    context.getString(R.string.early_learning),
                    getTranslatedValue(early_learning)
            ));

        return builder.toString();
    }

    private String getTranslatedValue(String nativeResult) {
        return nativeResult.equalsIgnoreCase("Yes") ?
                context.getString(R.string.yes) :
                context.getString(R.string.no);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(develop_warning_signs))
            return BaseAncHomeVisitAction.Status.PENDING;

        if(
                "No".equalsIgnoreCase(develop_warning_signs)
                        && "Yes".equalsIgnoreCase(stim_skills)
                        && (StringUtils.isBlank(early_learning) || "Yes".equalsIgnoreCase(early_learning))
        )
            return BaseAncHomeVisitAction.Status.COMPLETED;

        return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
    }
}
