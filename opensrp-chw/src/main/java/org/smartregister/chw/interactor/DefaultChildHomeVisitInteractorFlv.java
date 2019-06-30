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
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.chw.util.ObsIllnessDataModel;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.TaskServiceCalculate;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.List;

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

    protected ArrayList<ServiceTask> getCustomTasks(List<HomeVisitServiceDataModel> homeVisitServiceDataModels, CommonPersonObjectClient childClient, boolean isEditMode, Context context) {
        final ArrayList<ServiceTask> serviceTasks = new ArrayList<>();
        if (!isEditMode) {
            String dob = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);

            TaskServiceCalculate taskServiceCalculate = new TaskServiceCalculate(dob);
            ServiceTask serviceTaskDiversity = new ServiceTask();
            if (taskServiceCalculate.isDue(6) && !taskServiceCalculate.isExpire(60)) {
                serviceTaskDiversity.setTaskTitle(context.getResources().getString(R.string.minimum_dietary_title));
                serviceTaskDiversity.setTaskType(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name());
                serviceTasks.add(serviceTaskDiversity);
            }
            ServiceTask serviceTaskMuac = new ServiceTask();
            if (taskServiceCalculate.isDue(6) && !taskServiceCalculate.isExpire(60)) {
                serviceTaskMuac.setTaskTitle(context.getResources().getString(R.string.muac_title));
                serviceTaskMuac.setTaskType(TaskServiceCalculate.TASK_TYPE.MUAC.name());
                serviceTasks.add(serviceTaskMuac);
            }
//           ServiceTask serviceTaskLlitn = new ServiceTask();
//           if(!taskServiceCalculate.isExpire(60)){
//               serviceTaskLlitn.setTaskTitle(getContext().getResources().getString(R.string.llitn_title));
//               serviceTasks.add(serviceTaskLlitn);
//           }
//           ServiceTask serviceTaskEcd = new ServiceTask();
//           if(!taskServiceCalculate.isExpire(60)){
//               serviceTaskEcd.setTaskTitle(getContext().getResources().getString(R.string.ecd_title));
//               serviceTasks.add(serviceTaskEcd);
//           }
        } else {
            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitServiceDataModels) {
                if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.MINIMUM_DIETARY_DIVERSITY)) {
                    serviceTasks.add(ChildUtils.createDiateryFromEvent(context, homeVisitServiceDataModel.getHomeVisitDetails()));
                } else if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.MUAC)) {
                    serviceTasks.add(ChildUtils.createMuacFromEvent(context, homeVisitServiceDataModel.getHomeVisitDetails()));
                }
            }
        }
        return serviceTasks;
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

    protected Context getContext() {
        return ChwApplication.getInstance().getApplicationContext();
    }
}
