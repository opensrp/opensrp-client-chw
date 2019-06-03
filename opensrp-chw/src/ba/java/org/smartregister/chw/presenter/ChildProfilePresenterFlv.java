package org.smartregister.chw.presenter;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

public class ChildProfilePresenterFlv {
    static JSONObject generateForm(JSONObject jsonForm, String title) throws Exception {
        JSONObject form = FormUtils.getInstance(org.smartregister.family.util.Utils.context().applicationContext()).getFormJson(title);
        return form;
    }
}
