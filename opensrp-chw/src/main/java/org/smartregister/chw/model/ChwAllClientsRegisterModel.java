package org.smartregister.chw.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.UNIQUE_ID;

public class ChwAllClientsRegisterModel extends OpdRegisterActivityModel {

    private FormUtils formUtils;

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, @Nullable HashMap<String, String> injectedFieldValues) throws JSONException {
        JSONObject form = injectFields(formName, injectedFieldValues);

        if (StringUtils.isNotBlank(entityId)) {
            // Inject OPenSrp id into the form
            JSONObject stepOne = form.getJSONObject(OpdJsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(UNIQUE_ID)) {
                    jsonObject.remove(OpdJsonFormUtils.VALUE);
                    jsonObject.put(OpdJsonFormUtils.VALUE, entityId.replace("-", ""));
                }
            }
        }

        return form;
    }

    private JSONObject injectFields(String formName, @Nullable HashMap<String, String> injectedFieldValues)
            throws JSONException {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (injectedFieldValues != null && injectedFieldValues.size() > 0) {
            JSONObject stepOne = form.getJSONObject(OpdJsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fieldKey = jsonObject.getString(OpdJsonFormUtils.KEY);

                String fieldValue = injectedFieldValues.get(fieldKey);

                if (!TextUtils.isEmpty(fieldValue)) {
                    jsonObject.put(OpdJsonFormUtils.VALUE, fieldValue);
                }
            }
        }
        return form;
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    @Nullable
    @Override
    public List<OpdEventClient> processRegistration(String jsonString, FormTag formTag) {
        FamilyEventClient familyEventClient = JsonFormUtils.processFamilyMemberRegistrationForm(
                FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, null);

        if (familyEventClient == null) {
            return null;
        }

        updateWra(familyEventClient);
        List<OpdEventClient> opdEventClientList = new ArrayList<>();
        OpdEventClient opdEventClient = new OpdEventClient(familyEventClient.getClient(), familyEventClient.getEvent());
        opdEventClientList.add(opdEventClient);
        return opdEventClientList;
    }

    private void updateWra(FamilyEventClient familyEventClient) {
        // Add WRA
        Client client = familyEventClient.getClient();
        Event event = familyEventClient.getEvent();
        if (client != null && event != null && client.getGender().equalsIgnoreCase("female") && client.getBirthdate() != null) {
            DateTime date = new DateTime(client.getBirthdate());
            Years years = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
            int age = years.getYears();
            if (age >= 15 && age <= 49) {
                List<Object> list = new ArrayList<>();
                list.add("true");
                event.addObs(new Obs("concept", "text", "162849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "",
                        list, new ArrayList<>(), null, "wra"));
            }

        }
    }
}
