package org.smartregister.chw.shadows;

import org.json.JSONObject;
import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.anc.util.JsonFormUtils;

/**
 * @author rkodev
 */
@Implements(JsonFormUtils.class)
public class JsonFormUtilsShadow {

    private static JSONObject jsonObject = Mockito.mock(JSONObject.class);

    @Implementation
    public static JSONObject getFormAsJson(String formName) throws Exception {
        return jsonObject;
    }

    public static void setJsonObject(JSONObject jsonObject) {
        JsonFormUtilsShadow.jsonObject = jsonObject;
    }
}
