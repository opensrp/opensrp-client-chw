package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

import static org.smartregister.chw.util.Utils.formatDateForVisual;
import static org.smartregister.opd.utils.OpdConstants.DateFormat.YYYY_MM_DD;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {
    @Override
    public boolean isChildOverTwoMonths(CommonPersonObjectClient client) {
        return true; // This is because LMH isn't concerned with hiding the sick child form for children < 2 months
    }

    @Override
    public Intent getSickChildFormActivityIntent(JSONObject jsonObject, Context context) {
        return CoreJsonFormUtils.getJsonIntent(context, jsonObject, Utils.metadata().familyMemberFormActivity);
    }

    @Override
    public String getFormattedDateForVisual(String dueDate, String inputFormat) {
        return formatDateForVisual(dueDate, YYYY_MM_DD);
    }
}
