package org.smartregister.chw.interactor;

import android.content.Context;
import android.text.TextUtils;

import com.opensrp.chw.core.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.util.BirthCertDataModel;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.chw.util.ObsIllnessDataModel;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.TaskServiceCalculate;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.List;

import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT;
import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.ILLNESS_ACTION;
import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DATE;
import static com.opensrp.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;
import static com.opensrp.chw.core.utils.Utils.DD_MM_YYYY;
import static com.opensrp.chw.core.utils.Utils.convertToDateFormateString;

public abstract class DefaultChildHomeVisitInteractorFlv implements ChildHomeVisitInteractor.Flavor {
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
                serviceTaskDiversity.setTaskTitle(context.getString(R.string.minimum_dietary_title));
                serviceTaskDiversity.setTaskType(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name());
                serviceTasks.add(serviceTaskDiversity);
            }
            ServiceTask serviceTaskMuac = new ServiceTask();
            if (taskServiceCalculate.isDue(6) && !taskServiceCalculate.isExpire(60)) {
                serviceTaskMuac.setTaskTitle(context.getString(R.string.muac_title));
                serviceTaskMuac.setTaskType(TaskServiceCalculate.TASK_TYPE.MUAC.name());
                serviceTasks.add(serviceTaskMuac);
            }
            ServiceTask serviceTaskLlitn = new ServiceTask();
            if (!taskServiceCalculate.isExpire(60)) {
                serviceTaskLlitn.setTaskTitle(context.getString(R.string.llitn_title));
                serviceTaskLlitn.setTaskType(TaskServiceCalculate.TASK_TYPE.LLITN.name());
                serviceTasks.add(serviceTaskLlitn);
            }
            ServiceTask serviceTaskEcd = new ServiceTask();
            if (!taskServiceCalculate.isExpire(60)) {
                serviceTaskEcd.setTaskTitle(context.getString(R.string.ecd_title));
                serviceTaskEcd.setTaskType(TaskServiceCalculate.TASK_TYPE.ECD.name());
                serviceTasks.add(serviceTaskEcd);
            }
        } else {
            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitServiceDataModels) {
                if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.MINIMUM_DIETARY_DIVERSITY)) {
                    serviceTasks.add(ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name(),
                            homeVisitServiceDataModel.getHomeVisitDetails(),context.getString(R.string.minimum_dietary_title),
                            Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MINIMUM_DIETARY));
                } else if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.MUAC)) {
                    serviceTasks.add(ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.MUAC.name(), homeVisitServiceDataModel.getHomeVisitDetails(),
                            context.getString(R.string.muac_title),Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MUAC));
                }
                else if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.LLITN)) {
                    serviceTasks.add(ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.LLITN.name(), homeVisitServiceDataModel.getHomeVisitDetails(),
                            context.getString(R.string.llitn_title),Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_LLITN));
                }
                else if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(Constants.EventType.ECD)) {
                    try{
                        serviceTasks.add(ChildUtils.createECDTaskFromEvent(context,TaskServiceCalculate.TASK_TYPE.ECD.name(), homeVisitServiceDataModel.getHomeVisitDetails(),
                                context.getString(R.string.ecd_title)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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
                                birthCertDataModel.setBirthCertDate("Issued " + convertToDateFormateString(value, DD_MM_YYYY));
                            }

                            break;
                        case BIRTH_CERT_NUMBER:
                            String valueN = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if (!TextUtils.isEmpty(valueN)) {
                                birthCertDataModel.setBirthCertNumber("#" + valueN);
                            }

                            break;
                        default:
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

                            obsIllnessDataModel.setIllnessDate(convertToDateFormateString(value, DD_MM_YYYY));

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
