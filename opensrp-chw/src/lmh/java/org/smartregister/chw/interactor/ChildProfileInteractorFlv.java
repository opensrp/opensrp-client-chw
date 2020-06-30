package org.smartregister.chw.interactor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;

public class ChildProfileInteractorFlv implements ChildProfileInteractor.Flavour {
    @Override
    public void getFamilyName(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray) throws JSONException {
        String familyName = org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, false);
        jsonObject.put(JsonFormUtils.VALUE, familyName);
    }

}
