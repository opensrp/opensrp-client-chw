package org.smartgresiter.wcaro.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildHomeVisitContract;
import org.smartgresiter.wcaro.repository.WcaroRepository;
import org.smartgresiter.wcaro.util.BirthIllnessData;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
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

import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;

public class ChildHomeVisitInteractor implements ChildHomeVisitContract.Interactor {

    private static final String TAG = "VisitInteractor";
    private AppExecutors appExecutors;
    private HashMap<String, Pair<Client, Event>> saveList = new HashMap<>();
    private ArrayList<BirthIllnessData> birthCertDataList=new ArrayList<>();
    private ArrayList<BirthIllnessData> illnessDataList=new ArrayList<>();

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
    public ArrayList<BirthIllnessData> getIllnessDataList(){
        return illnessDataList;
    }
    public ArrayList<BirthIllnessData> getBirthCertDataList(){
        return birthCertDataList;
    }

    @Override
    public void generateBirthIllnessForm(String jsonString, final ChildHomeVisitContract.InteractorCallback callback) {
        try {
            Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
            if (pair == null) {
                return;
            }
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)) {
                birthCertDataList.clear();
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                BirthIllnessData birthIllnessData=new BirthIllnessData();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()){
                        case BIRTH_CERT:
                            birthIllnessData.setBirthCertHas(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE).equalsIgnoreCase("yes"));
//                            birthIllnessData=new BirthIllnessData();
//                            birthIllnessData.setQuestion(getContext().getString(R.string.has_birt_cert));
//                            birthIllnessData.setAnswer(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));
//                            birthCertDataList.add(birthIllnessData);
                            break;
                        case BIRTH_CERT_ISSUE_DATE:
                            String value=jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if(!TextUtils.isEmpty(value)){
                                birthIllnessData.setBirthCertDate("Issued "+Utils.convertToDateFormateString(value,Utils.dd_MMM_yyyy));
//                                birthIllnessData=new BirthIllnessData();
//                                birthIllnessData.setQuestion(getContext().getString(R.string.issuance_date));
//                                birthIllnessData.setAnswer(Utils.convertToDateFormateString(value,Utils.dd_MMM_yyyy));
//                                birthCertDataList.add(birthIllnessData);
                            }
                          
                            break;
                        case BIRTH_CERT_NUMBER:
                            String valueN=jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);
                            if(!TextUtils.isEmpty(valueN)){
                                birthIllnessData.setBirthCertNumber("#"+valueN);
//                                birthIllnessData=new BirthIllnessData();
//                                birthIllnessData.setQuestion(getContext().getString(R.string.number));
//                                birthIllnessData.setAnswer(valueN);
//                                birthCertDataList.add(birthIllnessData);
                            }
                          
                            break;

                    }

                }
                birthCertDataList.add(birthIllnessData);
//                String birthCert = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, BIRTH_CERT);
//                if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("no")) {
//                    String noti = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, BIRTH_CERT_NOTIFIICATION);
//                    birthIllnessData=new BirthIllnessData();
//                    birthIllnessData.setQuestion(getContext().getString(R.string.notification));
//                    birthIllnessData.setAnswer(noti);
//                    birthCertDataList.add(birthIllnessData);
//                }
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
                saveList.put(jsonString, pair);

            }
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)) {
                illnessDataList.clear();
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                BirthIllnessData birthIllnessData = new BirthIllnessData();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()){
                        case ILLNESS_DATE:
                            String value=jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);

                            birthIllnessData.setIllnessDate(Utils.convertToDateFormateString(value,Utils.dd_MMM_yyyy));
//                            birthIllnessData=new BirthIllnessData();
//                            birthIllnessData.setQuestion("Date");
//                            String value=jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);
//                            birthIllnessData.setAnswer(Utils.convertToDateFormateString(value,Utils.dd_MMM_yyyy));
//                            illnessDataList.add(birthIllnessData);
                            break;
                        case ILLNESS_DESCRIPTION:

                            birthIllnessData.setIllnessDescription(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));
//
//                            birthIllnessData=new BirthIllnessData();
//                            birthIllnessData.setQuestion("Description");
//                            birthIllnessData.setAnswer(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));
//                            illnessDataList.add(birthIllnessData);
                            break;
                        case ILLNESS_ACTION:
                            birthIllnessData.setActionTaken("Action taken: "+jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));
//                            birthIllnessData=new BirthIllnessData();
//                            birthIllnessData.setQuestion("Action taken");
//                            birthIllnessData.setAnswer(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));
//                            illnessDataList.add(birthIllnessData);
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
                saveList.put(jsonString, pair);

            }
        } catch (Exception e) {

        }

    }

    @Override
    public void saveForm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String json : saveList.keySet()) {
                    Pair<Client, Event> pair = saveList.get(json);
                    saveRegistration(pair, json);
                }
            }
        }).start();
    }

    private void processPopulatableFields(JSONObject previousJson, JSONObject jsonObject, JSONArray jsonArray) {
       try{
           jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,previousJson.getString(org.smartregister.family.util.JsonFormUtils.KEY));
       }catch (Exception e){

       }

    }

    private void saveRegistration(Pair<Client, Event> pair, String jsonString) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientjsonFromForm = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseClient));
                WcaroRepository pathRepository = new WcaroRepository(WcaroApplication.getInstance().getApplicationContext(), WcaroApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientJson = eventClientRepository.getClient(WcaroApplication.getInstance().getRepository().getReadableDatabase(), baseClient.getBaseEntityId());
                updateClientAttributes(clientjsonFromForm, clientJson);
                getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);

            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);

            }

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
    private Context getContext(){
        return WcaroApplication.getInstance().getApplicationContext();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
