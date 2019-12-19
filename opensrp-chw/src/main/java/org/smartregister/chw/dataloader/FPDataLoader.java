package org.smartregister.chw.dataloader;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.form_data.NativeFormsDataLoader;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.util.ArrayList;
import java.util.List;

public class FPDataLoader extends NativeFormsDataLoader {

    private String title;

    public FPDataLoader(String title) {
        this.title = title;
    }

    @Override
    public void bindNativeFormsMetaData(@NotNull JSONObject jsonObjectForm, Context context, String baseEntityID) throws JSONException {
        super.bindNativeFormsMetaData(jsonObjectForm, context, baseEntityID);
        JSONObject stepOne = jsonObjectForm.getJSONObject("step1");
        if (StringUtils.isNotBlank(title))
            stepOne.put("title", title);
    }

    @Override
    protected List<String> getEventTypes() {
        List<String> res = new ArrayList<>();
        res.add(FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION);
        res.add(FamilyPlanningConstants.EventType.UPDATE_FAMILY_PLANNING_REGISTRATION);
        return res;
    }

}
