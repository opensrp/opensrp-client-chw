package org.smartregister.chw.core.model;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class CoreFamilyRemoveMemberModel extends CoreFamilyProfileMemberModel implements FamilyRemoveMemberContract.Model {

    @Override
    public JSONObject prepareJsonForm(CommonPersonObjectClient client, String formType) {
        try {
            FormUtils formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            JSONObject form = formUtils.getFormJson(formType);

            form.put(JsonFormUtils.ENTITY_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
            // inject data into the form

            JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB)) {

                    String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                    if (StringUtils.isNotBlank(dobString)) {
                        Date dob = Utils.dobStringToDate(dobString);
                        if (dob != null) {
                            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, JsonFormUtils.dd_MM_yyyy.format(dob));
                            JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, "date_moved");
                            JSONObject date_died = CoreJsonFormUtils.getFieldJSONObject(jsonArray, "date_died");

                            // dobString = Utils.getDuration(dobString);
                            //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "";
                            int days = CoreJsonFormUtils.getDayFromDate(dobString);
                            min_date.put("min_date", "today-" + days + "d");
                            date_died.put("min_date", "today-" + days + "d");
                        }
                    }
                } else if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(CoreConstants.JsonAssets.DETAILS)) {

                    String dob = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                    String dobString = Utils.getDuration(dob);
                    dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

                    String details = String.format("%s %s %s, %s %s",
                            Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                            Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true),
                            Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true),
                            dobString,
                            Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, true)
                    );

                    jsonObject.put("text", details);

                }
            }

            return form;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getForm(CommonPersonObjectClient client) {
        Date dob = Utils.dobStringToDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        return ((dob != null && getDiffYears(dob, new Date()) >= 5) ? CoreConstants.JSON_FORM.getFamilyDetailsRemoveMember() : CoreConstants.JSON_FORM.getFamilyDetailsRemoveChild());
    }

    @Override
    public JSONObject prepareFamilyRemovalForm(String familyID, String familyName, String details) {
        try {
            FormUtils formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            JSONObject form = formUtils.getFormJson(CoreConstants.JSON_FORM.getFamilyDetailsRemoveFamily());
            form.put(JsonFormUtils.ENTITY_ID, familyID);

            JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(CoreConstants.JsonAssets.DETAILS)) {
                    jsonObject.put("text", details);
                }
                if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(CoreConstants.JsonAssets.FAM_NAME)) {
                    jsonObject.put("text", familyName);
                }
            }

            return form;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

}

