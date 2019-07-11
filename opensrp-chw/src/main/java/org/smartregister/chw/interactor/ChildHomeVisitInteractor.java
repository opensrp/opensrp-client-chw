package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.chw.util.BirthCertDataModel;
import org.smartregister.chw.util.ObsIllnessDataModel;
import org.smartregister.chw.util.BirthIllnessFormModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitInteractor implements ChildHomeVisitContract.Interactor {

    private static final String TAG = "VisitInteractor";
    private final String FORM_BIRTH = "birth_form";
    private final String FORM_ILLNESS = "illness_form";
    private AppExecutors appExecutors;
    private HashMap<String, BirthIllnessFormModel> saveList = new HashMap<>();
    private ArrayList<BirthCertDataModel> birthCertDataList = new ArrayList<>();
    private ArrayList<ObsIllnessDataModel> illnessDataList = new ArrayList<>();
    private Flavor flavor = new ChildHomeVisitInteractorFlv();

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

    public ArrayList<ObsIllnessDataModel> getIllnessDataList() {
        return illnessDataList;
    }

    public ArrayList<BirthCertDataModel> getBirthCertDataList() {
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
            flavor.generateServiceData(homeVisit);

        }
    }

    @Override
    public void generateBirthCertForm(final String jsonString,final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {
        birthCertDataList.clear();
        BirthCertDataModel birthCertDataModel = flavor.getBirthCertDataList(jsonString,isEditMode);
        if(birthCertDataModel !=null ){
            birthCertDataList.add(flavor.getBirthCertDataList(jsonString,isEditMode));
            Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
            if (pair == null) {
                return;
            }
            BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
            if (saveList.get(FORM_BIRTH) != null) {
                saveList.remove(FORM_BIRTH);
            }
            saveList.put(FORM_BIRTH, birthIllnessFormModel);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.updateBirthStatusTick(jsonString);
                        }
                    });
                }
            };
            appExecutors.diskIO().execute(runnable);
        }


    }

    @Override
    public void generateObsIllnessForm(final String jsonString, final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {
        illnessDataList.clear();
        ObsIllnessDataModel obsIllnessDataModel = flavor.getObsIllnessDataList(jsonString,isEditMode);
        if(obsIllnessDataModel !=null){
            illnessDataList.add(obsIllnessDataModel);
            Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
            if (pair == null) {
                return;
            }
            BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
            if (saveList.get(FORM_ILLNESS) != null) {
                saveList.remove(FORM_ILLNESS);
            }
            saveList.put(FORM_ILLNESS, birthIllnessFormModel);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.updateObsIllnessStatusTick(jsonString);
                        }
                    });
                }
            };
            appExecutors.diskIO().execute(runnable);

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
    public void generateTaskService(CommonPersonObjectClient childClient,final ChildHomeVisitContract.InteractorCallback callback, Context context, boolean isEditMode) {

       final ArrayList<ServiceTask> serviceTasks = flavor.getTaskService(childClient,isEditMode,context);


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

//
//            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
//            Date lastSyncDate = new Date(lastSyncTimeStamp);
//            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
//            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());


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

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Log.d(TAG, "onDestroy called");
    }

    public interface Flavor{
        ArrayList<ServiceTask> getTaskService(CommonPersonObjectClient childClient,boolean isEditMode,Context context);
        BirthCertDataModel getBirthCertDataList(String jsonString,boolean isEditMode);
        ObsIllnessDataModel getObsIllnessDataList(String jsonString,boolean isEditMode);
        void generateServiceData(HomeVisit homeVisit);
    }
}