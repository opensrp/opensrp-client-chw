package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.model.BirthIllnessModel;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.chw.util.TaskServiceCalculate;
import org.smartregister.chw.util.BirthIllnessData;
import org.smartregister.chw.util.BirthIllnessFormModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitInteractor implements ChildHomeVisitContract.Interactor {

    private static final String TAG = "VisitInteractor";
    private final String FORM_BIRTH = "birth_form";
    private final String FORM_ILLNESS = "illness_form";
    private AppExecutors appExecutors;
    private HashMap<String, BirthIllnessFormModel> saveList = new HashMap<>();
    private ArrayList<BirthIllnessData> birthCertDataList = new ArrayList<>();
    private ArrayList<BirthIllnessData> illnessDataList = new ArrayList<>();

    @VisibleForTesting
    ChildHomeVisitInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public ChildHomeVisitInteractor() {
        this(new AppExecutors());
    }

    public int getSaveSize() {
        return saveList.size();
    }

    public ArrayList<BirthIllnessData> getIllnessDataList() {
        return illnessDataList;
    }

    public ArrayList<BirthIllnessData> getBirthCertDataList() {
        return birthCertDataList;
    }

    @Override
    public void getLastEditData(CommonPersonObjectClient childClient, final ChildHomeVisitContract.InteractorCallback callback) {

        String lastHomeVisitStr = getValue(childClient, ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
        HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
        if (homeVisit != null) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(homeVisit.getBirthCertificationState().toString());
                String birt = jsonObject.getString("birtCert");
                callback.updateBirthCertEditData(birt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonObject = new JSONObject(homeVisit.getIllness_information().toString());
                String illness = jsonObject.getString("obsIllness");
                callback.updateObsIllnessEditData(illness);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
//        getLastVisitBirthCertData(childClient)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BirthIllnessFormModel>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(BirthIllnessFormModel birthIllnessModel) {
//                        callback.updateBirthCertEditData(birthIllnessModel.getLastBirthCertData());
//                        callback.updateObsIllnessEditData(birthIllnessModel.getLastIllnessData());
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    public Observable<BirthIllnessModel> getLastVisitBirthCertData(final CommonPersonObjectClient childClient) {
        return Observable.create(new ObservableOnSubscribe<BirthIllnessModel>() {
            @Override
            public void subscribe(ObservableEmitter<BirthIllnessModel> emmiter) throws Exception {
                String lastHomeVisitStr = getValue(childClient, ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
                HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
                if (homeVisit != null) {
                    BirthIllnessModel birthIllnessModel = new BirthIllnessModel();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(homeVisit.getBirthCertificationState().toString());
                        String birt = jsonObject.getString("birtCert");
                        birthIllnessModel.setLastBirthCertData(birt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject = new JSONObject(homeVisit.getIllness_information().toString());
                        String illness = jsonObject.getString("obsIllness");
                        birthIllnessModel.setLastIllnessData(illness);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    emmiter.onNext(birthIllnessModel);

                }

            }
        });
    }

    @Override
    public void generateBirthIllnessForm(String jsonString, final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {
        try {
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)) {
                birthCertDataList.clear();
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                BirthIllnessData birthIllnessData = new BirthIllnessData();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
                        case BIRTH_CERT:
                            birthIllnessData.setBirthCertHas(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE).equalsIgnoreCase("yes"));
                            break;
                        case BIRTH_CERT_ISSUE_DATE:
                            String value = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if (!TextUtils.isEmpty(value)) {
                                birthIllnessData.setBirthCertDate("Issued " + Utils.convertToDateFormateString(value, Utils.dd_MMM_yyyy));
                            }

                            break;
                        case BIRTH_CERT_NUMBER:
                            String valueN = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if (!TextUtils.isEmpty(valueN)) {
                                birthIllnessData.setBirthCertNumber("#" + valueN);
                            }

                            break;

                    }

                }
                birthCertDataList.add(birthIllnessData);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.updateBirthStatusTick();
                            }
                        });
                    }
                };
                appExecutors.diskIO().execute(runnable);
                //if(!isEditMode){
                Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
                if (pair == null) {
                    return;
                }
                BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
                if (saveList.get(FORM_BIRTH) != null) {
                    saveList.remove(FORM_BIRTH);
                }
                saveList.put(FORM_BIRTH, birthIllnessFormModel);


                //}

            }
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)) {
                illnessDataList.clear();
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                BirthIllnessData birthIllnessData = new BirthIllnessData();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
                        case ILLNESS_DATE:
                            String value = jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE);

                            birthIllnessData.setIllnessDate(Utils.convertToDateFormateString(value, Utils.dd_MMM_yyyy));

                            break;
                        case ILLNESS_DESCRIPTION:

                            birthIllnessData.setIllnessDescription(jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE));

                            break;
                        case ILLNESS_ACTION:
                            birthIllnessData.setActionTaken("Action taken: " + jsonObject.optString(org.smartregister.family.util.JsonFormUtils.VALUE));

                            break;
                        default:
                            break;
                    }

                }
                illnessDataList.add(birthIllnessData);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.updateObsIllnessStatusTick();
                            }
                        });
                    }
                };
                appExecutors.diskIO().execute(runnable);
                //if(!isEditMode){
                Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
                if (pair == null) {
                    return;
                }
                BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
                if (saveList.get(FORM_ILLNESS) != null) {
                    saveList.remove(FORM_ILLNESS);
                }
                saveList.put(FORM_ILLNESS, birthIllnessFormModel);
                //}

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void saveForm(CommonPersonObjectClient childClient) {
        for (String json : saveList.keySet()) {
            BirthIllnessFormModel birthIllnessFormModel = saveList.get(json);
            saveRegistration(birthIllnessFormModel.getPair(), childClient);
        }
    }

    @Override
    public void generateTaskService(CommonPersonObjectClient childClient,final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {
        final ArrayList<ServiceTask> serviceTasks = new ArrayList<>();
        if(!isEditMode){
           String dob = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);

           TaskServiceCalculate taskServiceCalculate = new TaskServiceCalculate(dob);
           ServiceTask serviceTaskDiversity = new ServiceTask();
           if(taskServiceCalculate.isDue(6) && !taskServiceCalculate.isExpire(60)){
               serviceTaskDiversity.setTaskTitle(getContext().getResources().getString(R.string.minimum_dietary_title));
               serviceTaskDiversity.setTaskType(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name());
               serviceTasks.add(serviceTaskDiversity);
           }
           ServiceTask serviceTaskMuac = new ServiceTask();
           if(taskServiceCalculate.isDue(6) && !taskServiceCalculate.isExpire(60)){
               serviceTaskMuac.setTaskTitle(getContext().getResources().getString(R.string.muac_title));
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
       }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.updateTaskAdapter(serviceTasks);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);


    }

    private void saveRegistration(Pair<Client, Event> pair, CommonPersonObjectClient childClient) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientjsonFromForm = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseClient));
                ChwRepository pathRepository = new ChwRepository(ChwApplication.getInstance().getApplicationContext(), ChwApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientJson = eventClientRepository.getClient(ChwApplication.getInstance().getRepository().getReadableDatabase(), baseClient.getBaseEntityId());
                updateClientAttributes(clientjsonFromForm, clientJson);
                String birthCert = getValue(childClient.getColumnmaps(), BIRTH_CERT, true);
                if (TextUtils.isEmpty(birthCert)) {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                } else {
                    org.smartregister.family.util.JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                }

            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

//            if (baseClient != null || baseEvent != null) {
//                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
//                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
//
//            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());


        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private static void updateClientAttributes(JSONObject clientjsonFromForm, JSONObject clientJson) {
        try {
            JSONObject formAttributes = clientjsonFromForm.getJSONObject("attributes");
            JSONObject clientAttributes = clientJson.getJSONObject("attributes");
            Iterator<String> keys = formAttributes.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                clientAttributes.put(key, formAttributes.get(key));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return org.smartregister.family.util.Utils.context().allSharedPreferences();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

    private Context getContext() {
        return ChwApplication.getInstance().getApplicationContext();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Log.d(TAG, "onDestroy called");
    }
}