package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Date;

public class ChildProfileInteractor implements ChildProfileContract.Interactor {
    public static final String TAG = ChildProfileInteractor.class.getName();

    private AppExecutors appExecutors;
    private CommonPersonObjectClient pClient;

    public CommonPersonObjectClient getpClient() {
        return pClient;
    }

    public void setpClient(CommonPersonObjectClient pClient) {
        this.pClient = pClient;
    }

    @VisibleForTesting
    ChildProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }
    public ChildProfileInteractor() {
        this(new AppExecutors());
    }
    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public void refreshProfileView(final String baseEntityId, final boolean isForEdit, final ChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String query=mainSelect(ChildDBConstants.KEY.TABLE_NAME,ChildDBConstants.KEY.PARENT_TABLE_NAME,baseEntityId);

                Cursor cursor=getCommonRepository(ChildDBConstants.KEY.TABLE_NAME).rawCustomQueryForAdapter(query);
                if(cursor!=null && cursor.moveToFirst()){
                    CommonPersonObject personObject = getCommonRepository(ChildDBConstants.KEY.TABLE_NAME).readAllcommonforCursorAdapter(cursor);
                    pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                        pClient.setColumnmaps(personObject.getColumnmaps());
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (isForEdit) {
                                callback.startFormForEdit(pClient);
                            } else {
                                callback.refreshProfileTopSection(pClient);
                            }
                        }
                    });
                        cursor.close();
                }



            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple,final ChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final Pair<Client, Event> pair,final String jsonString,final boolean isEditMode,final ChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(pair, jsonString, isEditMode);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public String mainSelect(String tableName,String parentTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName,parentTableName));
        queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " +
                "" + parentTableName + ".id where "+tableName+"."+DBConstants.KEY.BASE_ENTITY_ID+" = '"+mainCondition+"'");
        return queryBUilder.mainCondition("");
    }

    protected String[] mainColumns(String tableName,String parentTableName) {

        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.RELATIONAL_ID +" as " +"relationalid",
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                parentTableName + "."+DBConstants.KEY.FIRST_NAME+" as "+ChildDBConstants.KEY.FAMILY_FIRST_NAME,
                parentTableName + "."+DBConstants.KEY.LAST_NAME+" as "+ChildDBConstants.KEY.FAMILY_LAST_NAME,
                parentTableName + "."+DBConstants.KEY.VILLAGE_TOWN+" as "+ChildDBConstants.KEY.FAMILY_HOME_ADDRESS,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.UNIQUE_ID,
                tableName + "." + DBConstants.KEY.GENDER,
                tableName + "." + DBConstants.KEY.DOB};
        return columns;
    }
    private void saveRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                if (isEditMode) {
                    JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                } else {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (isEditMode) {
                // Unassign current OPENSRP ID
                if (baseClient != null) {
                    String newOpenSRPId = baseClient.getIdentifier(DBConstants.KEY.UNIQUE_ID).replace("-", "");
                    String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                }

            } else {
                if (baseClient != null) {
                    String opensrpId = baseClient.getIdentifier(DBConstants.KEY.UNIQUE_ID);

                    //mark OPENSRP ID as used
                    getUniqueIdRepository().close(opensrpId);
                }
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }
    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }
}
