package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildHomeVisitContract;
import org.smartgresiter.wcaro.repository.WcaroRepository;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ChildHomeVisitInteractor implements ChildHomeVisitContract.Interactor {

    private static final String TAG = "VisitInteractor";
    private AppExecutors appExecutors;
    private HashMap<String, Pair<Client, Event>> saveList = new HashMap<>();

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

    @Override
    public void generateBirthIllnessForm(String jsonString, final ChildHomeVisitContract.InteractorCallback callback) {
        try {
            Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
            if (pair == null) {
                return;
            }
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)) {
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

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
