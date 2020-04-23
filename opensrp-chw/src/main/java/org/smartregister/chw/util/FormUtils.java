package org.smartregister.chw.util;

import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ClientForm;
import org.smartregister.repository.ClientFormRepository;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-22.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class FormUtils {
    public static JSONObject getFormJson(String formIdentity, org.smartregister.util.FormUtils formUtils,String locale) {
        ClientFormRepository clientFormRepository = CoreLibrary.getInstance().context().getClientFormRepository();

        //Check the current locale of the app to load the correct version of the form in the desired language
        String localeFormIdentity = formIdentity;
        if(!locale.equals("en")){
            localeFormIdentity = localeFormIdentity+"-"+locale;
        }
        ClientForm clientForm = clientFormRepository.getActiveClientFormByIdentifier(localeFormIdentity);
        try {
            if (clientForm != null) {
                Timber.i("%s form loaded from db", formIdentity);
                String jsonString = convertStandardJSONString(clientForm.getJson());
                return new JSONObject(jsonString);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        Timber.i("%s form loaded from Assets", formIdentity);
        return formUtils.getFormJson(formIdentity);
    }

    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        data_json = data_json.replace("\\n","");
        data_json = data_json.replace("\\\"","\"");
        return data_json;
    }
}
