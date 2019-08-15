package org.smartregister.chw.core.interactor;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
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

import timber.log.Timber;

public abstract class CoreFamilyRemoveMemberInteractor implements FamilyRemoveMemberContract.Interactor {

    protected CoreChwApplication coreChwApplication;
    private AppExecutors appExecutors;

    public CoreFamilyRemoveMemberInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    CoreFamilyRemoveMemberInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void removeMember(final String familyID, final String lastLocationId, final JSONObject exitForm, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = () -> {
            String value = null;
            try {
                // process the json object
                value = removeUser(familyID, exitForm, lastLocationId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String finalValue = value;
            appExecutors.mainThread().execute(() -> presenter.memberRemoved(finalValue));
        };

        appExecutors.diskIO().execute(runnable);

    }

    @Override
    public void processFamilyMember(final String familyID, final CommonPersonObjectClient client, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = () -> {

            final HashMap<String, String> res = new HashMap<>();
            String info_columns = CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER + " , " +
                    CoreConstants.RELATIONSHIP.FAMILY_HEAD;

            String sql = String.format("select %s from %s where %s = '%s' ",
                    info_columns,
                    Utils.metadata().familyRegister.tableName,
                    DBConstants.KEY.BASE_ENTITY_ID,
                    familyID
            );

            CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

            Cursor cursor = null;
            try {
                cursor = commonRepository.queryTable(sql);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    int columncount = cursor.getColumnCount();

                    for (int i = 0; i < columncount; i++) {
                        res.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
                    }

                    cursor.moveToNext();
                }
            } catch (Exception e) {
                Timber.e(e, e.toString());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            appExecutors.mainThread().execute(() -> presenter.processMember(res, client));
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getFamilySummary(final String familyID, final FamilyRemoveMemberContract.InteractorCallback<HashMap<String, String>> callback) {

        Runnable runnable;
        try {
            runnable = new Runnable() {

                Integer kids = getCount(CoreConstants.TABLE_NAME.CHILD, familyID);
                Integer members = getCount(CoreConstants.TABLE_NAME.FAMILY_MEMBER, familyID);

                EventClientRepository eventClientRepository = FamilyLibrary.getInstance().context().getEventClientRepository();
                JSONObject familyJSON = eventClientRepository.getClientByBaseEntityId(familyID);

                String name = (String) familyJSON.get("firstName");

                @Override
                public void run() {
                    appExecutors.mainThread().execute(() -> {

                        HashMap<String, String> results = new HashMap<>();
                        results.put(CoreConstants.TABLE_NAME.CHILD, kids.toString());
                        results.put(CoreConstants.TABLE_NAME.FAMILY_MEMBER, members.toString());
                        results.put(CoreConstants.GLOBAL.NAME, name);
                        callback.onResult(results);
                    });
                }

            };
        } catch (final Exception e) {
            e.printStackTrace();

            runnable = () -> appExecutors.mainThread().execute(() -> callback.onError(e));
        }

        appExecutors.diskIO().execute(runnable);

    }

    private int getCount(String tableName, String familyID) throws Exception {

        int count;
        Cursor c = null;
        String mainCondition = String.format(" %s = '%s' and %s is null ", DBConstants.KEY.RELATIONAL_ID, familyID, DBConstants.KEY.DATE_REMOVED);
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = sqb.queryForCountOnRegisters(tableName, mainCondition);
            query = sqb.Endquery(query);
            Timber.i("2%s", query);
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

    private CommonRepository commonRepository(String tableName) {
        return coreChwApplication.getContext().commonrepository(tableName);
    }

    private String removeUser(String familyID, JSONObject closeFormJsonString, String providerId) throws Exception {
        String res = null;
        Triple<Pair<Date, String>, String, List<Event>> triple = CoreJsonFormUtils.processRemoveMemberEvent(familyID, Utils.getAllSharedPreferences(), closeFormJsonString, providerId);
        if (triple != null && triple.getLeft() != null) {
            processEvents(triple.getRight());

            if (triple.getLeft().second.equalsIgnoreCase(CoreConstants.EventType.REMOVE_CHILD)) {
                updateRepo(triple, Utils.metadata().familyMemberRegister.tableName);
                updateRepo(triple, CoreConstants.TABLE_NAME.CHILD);
            } else if (triple.getLeft().second.equalsIgnoreCase(CoreConstants.EventType.REMOVE_FAMILY)) {
                updateRepo(triple, Utils.metadata().familyRegister.tableName);
            } else {
                updateRepo(triple, Utils.metadata().familyMemberRegister.tableName);
            }
            res = triple.getLeft().second;
        }

        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
        getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        return res;
    }

    private void processEvents(List<Event> events) throws JSONException {
        ECSyncHelper syncHelper = coreChwApplication.getEcSyncHelper();
        for (Event e : events) {
            syncHelper.addEvent(e.getBaseEntityId(), new JSONObject(CoreJsonFormUtils.gson.toJson(e)));
        }
    }

    private void updateRepo(Triple<Pair<Date, String>, String, List<Event>> triple, String tableName) {
        AllCommonsRepository commonsRepository = coreChwApplication.getAllCommonsRepository(tableName);

        Date date_removed = new Date();
        Date dod = null;
        if (triple.getLeft() != null && triple.getLeft().first != null) {
            dod = triple.getLeft().first;
        }

        if (commonsRepository != null && dod == null) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, getDBFormatedDate(date_removed));
            commonsRepository.update(tableName, values, triple.getMiddle());
            commonsRepository.updateSearch(triple.getMiddle());
            commonsRepository.close(triple.getMiddle());
        }

        // enter the date of death
        if (dod != null && commonsRepository != null) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DOD, getDBFormatedDate(dod));
            commonsRepository.update(tableName, values, triple.getMiddle());
            commonsRepository.updateSearch(triple.getMiddle());
        }
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    private String getDBFormatedDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    protected abstract void setCoreChwApplication();
}
