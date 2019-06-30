package org.smartregister.chw.interactor;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.util.BirthCertDataModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.ObsIllnessDataModel;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;

public class DefaultChildHomeVisitInteractorFlv implements ChildHomeVisitInteractor.Flavor {

    @Override
    public ArrayList<ServiceTask> getTaskService(CommonPersonObjectClient childClient, boolean isEditMode, Context context) {

        return new ArrayList<>();
    }

    @Override
    public BirthCertDataModel getBirthCertDataList(String jsonString, boolean isEditMode) {

        JSONObject form;
        try {
            form = new JSONObject(jsonString);
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)) {
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                BirthCertDataModel birthCertDataModel = new BirthCertDataModel();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
                        case BIRTH_CERT:
                            birthCertDataModel.setBirthCertHas(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE).equalsIgnoreCase("yes"));
                            break;
                        case BIRTH_CERT_ISSUE_DATE:
                            String value = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if (!TextUtils.isEmpty(value)) {
                                birthCertDataModel.setBirthCertDate("Issued " + Utils.convertToDateFormateString(value, Utils.dd_MMM_yyyy));
                            }

                            break;
                        case BIRTH_CERT_NUMBER:
                            String valueN = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if (!TextUtils.isEmpty(valueN)) {
                                birthCertDataModel.setBirthCertNumber("#" + valueN);
                            }

                            break;

                    }

                }
                return birthCertDataModel;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public ObsIllnessDataModel getObsIllnessDataList(String jsonString, boolean isEditMode) {
        JSONObject form;
        try {
            form = new JSONObject(jsonString);
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)) {
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                ObsIllnessDataModel obsIllnessDataModel = new ObsIllnessDataModel();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
                        case ILLNESS_DATE:
                            String value = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);

                            obsIllnessDataModel.setIllnessDate(Utils.convertToDateFormateString(value, Utils.dd_MMM_yyyy));

                            break;
                        case ILLNESS_DESCRIPTION:

                            obsIllnessDataModel.setIllnessDescription(jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE));

                            break;
                        case ILLNESS_ACTION:
                            obsIllnessDataModel.setActionTaken(getContext().getString(R.string.action_taken) + ": " + jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE));

                            break;
                        default:
                            break;
                    }

                }
                return obsIllnessDataModel;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void generateServiceData(HomeVisit homeVisit) {
        // no need to do anything
    }

    private Context getContext() {
        return ChwApplication.getInstance().getApplicationContext();
    }
}
