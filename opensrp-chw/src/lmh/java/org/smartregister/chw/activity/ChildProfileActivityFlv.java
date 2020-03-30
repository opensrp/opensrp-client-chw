package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {
    @Override
    public boolean isChildOverTwoMonths(CommonPersonObjectClient client) {
        return true; // This is because LMH isn't concerned with hiding the sick child form for children < 2 months
    }

    @Override
    public Intent getSickChildFormActivityIntent(JSONObject jsonObject, Context context) {
        return CoreJsonFormUtils.getJsonIntent(context, jsonObject, Utils.metadata().familyMemberFormActivity);
    }
}
