package org.smartgresiter.wcaro.interactor;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FamilyRemoveMemberInteractor implements FamilyRemoveMemberContract.Interactor {

    private static String TAG = FamilyRemoveMemberInteractor.class.getCanonicalName();

    private AppExecutors appExecutors;

    private static FamilyRemoveMemberInteractor instance;

    public static FamilyRemoveMemberInteractor getInstance() {
        if (instance == null)
            instance = new FamilyRemoveMemberInteractor();

        return instance;
    }

    @VisibleForTesting
    FamilyRemoveMemberInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyRemoveMemberInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void removeMember(final String familyID, final String lastLocationId, final JSONObject exitForm, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Boolean res = true;
                String value = null;
                // process the json object
                try {
                    value = removeUser(familyID, exitForm, lastLocationId);
                } catch (Exception e) {
                    res = false;
                    e.printStackTrace();
                }

                final String finalValue = value;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.memberRemoved(finalValue);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    @Override
    public void processFamilyMember(final String familyID, final CommonPersonObjectClient client, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final HashMap<String, String> res = new HashMap<>();
                String info_columns = Constants.RELATIONSHIP.PRIMARY_CAREGIVER + " , " +
                        Constants.RELATIONSHIP.FAMILY_HEAD;

                String sql = String.format("select %s from %s where %s = '%s' ",
                        info_columns,
                        Utils.metadata().familyRegister.tableName,
                        DBConstants.KEY.BASE_ENTITY_ID,
                        familyID
                );

                CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

                Cursor cursor = commonRepository.queryTable(sql);
                try {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        int columncount = cursor.getColumnCount();

                        for (int i = 0; i < columncount; i++) {
                            res.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
                        }

                        cursor.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                } finally {
                    cursor.close();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.processMember(res, client);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getFamilySummary(final String familyID, final FamilyRemoveMemberContract.InteractorCallback<HashMap<String, String>> callback) {

        Runnable runnable = null;
        try {
            runnable = new Runnable() {

                Integer kids = getCount(Constants.TABLE_NAME.CHILD, familyID);
                Integer members = getCount(Constants.TABLE_NAME.FAMILY_MEMBER, familyID);

                EventClientRepository eventClientRepository = FamilyLibrary.getInstance().context().getEventClientRepository();
                JSONObject familyJSON = eventClientRepository.getClientByBaseEntityId(familyID);

                String name = (String) familyJSON.get("firstName");

                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            HashMap<String, String> results = new HashMap<>();
                            results.put(Constants.TABLE_NAME.CHILD, kids.toString());
                            results.put(Constants.TABLE_NAME.FAMILY_MEMBER, members.toString());
                            results.put(Constants.GLOBAL.NAME, name);
                            callback.onResult(results);
                        }
                    });
                }

            };
        } catch (final Exception e) {
            e.printStackTrace();

            runnable = new Runnable() {

                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            };
        }

        appExecutors.diskIO().execute(runnable);

    }

    private int getCount(String tableName, String familyID) throws Exception {

        Integer count = null;
        Cursor c = null;
        String mainCondition = String.format(" %s = '%s' and %s is null ", DBConstants.KEY.RELATIONAL_ID, familyID, DBConstants.KEY.DATE_REMOVED);
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = sqb.queryForCountOnRegisters(tableName, mainCondition);
            query = sqb.Endquery(query);
            Log.i(getClass().getName(), "2" + query);
            c = commonRepository(tableName).rawCustomQueryForAdapter(query);
            if (c.moveToFirst()) {
                count = c.getInt(0);
            } else {
                count = 0;
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return count;
    }

    private boolean isValidFilterForFts(CommonRepository commonRepository, String filters) {
        return commonRepository.isFts() && filters != null && !StringUtils
                .containsIgnoreCase(filters, "like") && !StringUtils
                .startsWithIgnoreCase(filters.trim(), "and ");
    }

    private CommonRepository commonRepository(String tableName) {
        return WcaroApplication.getInstance().getContext().commonrepository(tableName);
    }

    private String removeUser(String familyID, JSONObject closeFormJsonString, String providerId) throws Exception {

        String res = null;
        Triple<Pair<Date, String>, String, List<Event>> triple = JsonFormUtils.processRemoveMemberEvent(familyID, Utils.getAllSharedPreferences(), closeFormJsonString, providerId);
        if (triple != null) {
            if (triple.getLeft() != null) {

                processEvents(triple.getRight());


                if (triple.getLeft().second.equalsIgnoreCase(Constants.EventType.REMOVE_CHILD)) {
                    updateRepo(triple, Utils.metadata().familyMemberRegister.tableName);
                    updateRepo(triple, Constants.TABLE_NAME.CHILD);
                } else if (triple.getLeft().second.equalsIgnoreCase(Constants.EventType.REMOVE_FAMILY)) {
                    updateRepo(triple, Utils.metadata().familyRegister.tableName);
                } else {
                    updateRepo(triple, Utils.metadata().familyMemberRegister.tableName);
                }
                res = triple.getLeft().second;
            }
        }

        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
        getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        return res;
    }

    private void updateRepo(Triple<Pair<Date, String>, String, List<Event>> triple, String tableName) {
        AllCommonsRepository commonsRepository = WcaroApplication.getInstance().getAllCommonsRepository(tableName);

        if (commonsRepository != null) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, getDBFormatedDate(new Date()));
            commonsRepository.update(tableName, values, triple.getMiddle());
            commonsRepository.updateSearch(triple.getMiddle());
            commonsRepository.close(triple.getMiddle());
        }

        // enter the date of death
        if (triple.getLeft() != null && triple.getLeft().first != null && commonsRepository != null) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DOD, getDBFormatedDate(triple.getLeft().first));
            commonsRepository.update(tableName, values, triple.getMiddle());
            commonsRepository.updateSearch(triple.getMiddle());
        }
    }

    private void processEvents(List<Event> events) throws JSONException {
        ECSyncHelper syncHelper = WcaroApplication.getInstance().getEcSyncHelper();
        for (Event e : events) {
            syncHelper.addEvent(e.getBaseEntityId(), new JSONObject(JsonFormUtils.gson.toJson(e)));
        }
    }

    private String getDBFormatedDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }
}
