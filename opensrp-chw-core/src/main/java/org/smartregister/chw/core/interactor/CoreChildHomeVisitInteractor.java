package org.smartregister.chw.core.interactor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ChildHomeVisitContract;
import org.smartregister.chw.core.domain.HomeVisit;
import org.smartregister.chw.core.model.BirthIllnessFormModel;
import org.smartregister.chw.core.utils.BirthCertDataModel;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.ObsIllnessDataModel;
import org.smartregister.chw.core.utils.ServiceTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import timber.log.Timber;

import static org.smartregister.util.Utils.getValue;

public class CoreChildHomeVisitInteractor implements ChildHomeVisitContract.Interactor {
    private static final String TAG = "VisitInteractor";
    public AppExecutors appExecutors;
    public HashMap<String, BirthIllnessFormModel> saveList = new HashMap<>();
    private ArrayList<BirthCertDataModel> birthCertDataList = new ArrayList<>();
    private ArrayList<ObsIllnessDataModel> illnessDataList = new ArrayList<>();

    public Flavor flavor;

    public Flavor getFlavor() {
        return flavor;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }


    public static void updateClientAttributes(JSONObject clientjsonFromForm, JSONObject clientJson) {
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
        if (flavor != null) {
        String lastHomeVisitStr = Utils.getValue(childClient, ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
        HomeVisit homeVisit = CoreChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
        if (homeVisit != null) {
            JSONObject jsonObject;
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
    }

    @Override
    public void generateBirthCertForm(final String jsonString, final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {

        if (flavor != null) {
            birthCertDataList.clear();
            BirthCertDataModel birthCertDataModel = flavor.getBirthCertDataList(jsonString, isEditMode);
            if (birthCertDataModel != null) {
                birthCertDataList.add(flavor.getBirthCertDataList(jsonString, isEditMode));
                Pair<Client, Event> pair = CoreJsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
                if (pair == null) {
                    return;
                }
                BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
                if (saveList.get("birth_form") != null) {
                    saveList.remove("birth_form");
                }
                saveList.put("birth_form", birthIllnessFormModel);
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
    }

    @Override
    public void generateObsIllnessForm(final String jsonString, final ChildHomeVisitContract.InteractorCallback callback, boolean isEditMode) {

        if (flavor != null) {
            illnessDataList.clear();
            ObsIllnessDataModel obsIllnessDataModel = flavor.getObsIllnessDataList(jsonString, isEditMode);
            if (obsIllnessDataModel != null) {
                illnessDataList.add(obsIllnessDataModel);
                Pair<Client, Event> pair = CoreJsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(), jsonString);
                if (pair == null) {
                    return;
                }
                BirthIllnessFormModel birthIllnessFormModel = new BirthIllnessFormModel(jsonString, pair);
                if (saveList.get("illness_form") != null) {
                    saveList.remove("illness_form");
                }
                saveList.put("illness_form", birthIllnessFormModel);
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
    }

    @Override
    public void generateTaskService(CommonPersonObjectClient childClient, final ChildHomeVisitContract.InteractorCallback callback, Context context, boolean isEditMode) {
        if (flavor != null) {
            final ArrayList<ServiceTask> serviceTasks = flavor.getTaskService(childClient, isEditMode, context);


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
    }

    @Override
    public void saveForm(CommonPersonObjectClient childClient) {
        //// TODO: 02/08/19
    }


    public AllSharedPreferences getAllSharedPreferences() {
        return org.smartregister.family.util.Utils.context().allSharedPreferences();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Timber.d("onDestroy called");
    }

    public interface Flavor {
        ArrayList<ServiceTask> getTaskService(CommonPersonObjectClient childClient, boolean isEditMode, Context context);

        BirthCertDataModel getBirthCertDataList(String jsonString, boolean isEditMode);

        ObsIllnessDataModel getObsIllnessDataList(String jsonString, boolean isEditMode);

        void generateServiceData(HomeVisit homeVisit);
    }
}