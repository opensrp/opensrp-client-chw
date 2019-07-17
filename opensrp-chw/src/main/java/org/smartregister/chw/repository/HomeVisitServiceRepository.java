package org.smartregister.chw.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class HomeVisitServiceRepository extends BaseRepository {
    private static final String TAG = HomeVisitServiceRepository.class.getCanonicalName();
    private static final String HOME_VISIT_SERVICE_SQL = "CREATE TABLE home_visit_service (home_visit_id VARCHAR NOT NULL,event_type VARCHAR,details TEXT,date DATETIME NOT NULL)";
    public static final String HOME_VISIT_SERVICE_TABLE_NAME = "home_visit_service";
    public static final String HOME_VISIT_ID = "home_visit_id";
    public static final String EVENT_TYPE = "event_type";
    public static final String DATE = "date";
    public static final String DETAILS = "details";

    public static final String[] TABLE_COLUMNS = {HOME_VISIT_ID, EVENT_TYPE, DATE, DETAILS};

    public HomeVisitServiceRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(HOME_VISIT_SERVICE_SQL);
    }

    public void add(HomeVisitServiceDataModel homeVisitServiceDataModel) {
        if (homeVisitServiceDataModel == null) {
            return;
        }
        try {
            SQLiteDatabase database = getWritableDatabase();
            if (homeVisitServiceDataModel.getHomeVisitId() != null) {
                HomeVisitServiceDataModel uniqueVisit = findUnique(database, homeVisitServiceDataModel);
                if (uniqueVisit != null) {
                    update(database, homeVisitServiceDataModel);
                } else {
                    database.insert(HOME_VISIT_SERVICE_TABLE_NAME, null, createValuesFor(homeVisitServiceDataModel));

                }
            }

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    public HomeVisitServiceDataModel findUnique(SQLiteDatabase db, HomeVisitServiceDataModel homeVisitServiceDataModel) {
        if (homeVisitServiceDataModel == null || TextUtils.isEmpty(homeVisitServiceDataModel.getHomeVisitId())) {
            return null;
        }
        SQLiteDatabase database = (db == null) ? getReadableDatabase() : db;
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(homeVisitServiceDataModel.getHomeVisitId())) {
            selection = HOME_VISIT_ID + " = ? " + COLLATE_NOCASE + " and " + EVENT_TYPE + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{homeVisitServiceDataModel.getHomeVisitId(), homeVisitServiceDataModel.getEventType()};
        }
        net.sqlcipher.Cursor cursor = database.query(HOME_VISIT_SERVICE_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null, null);
        List<HomeVisitServiceDataModel> homeVisitList = getAllHomeVisitService(cursor);
        if (!homeVisitList.isEmpty()) {
            return homeVisitList.get(0);
        }
        return null;
    }

    public List<HomeVisitServiceDataModel> getHomeVisitServiceList(String homeVisitId) {
        SQLiteDatabase database = getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(homeVisitId)) {
            selection = HOME_VISIT_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{homeVisitId};
        }
        net.sqlcipher.Cursor cursor = database.query(HOME_VISIT_SERVICE_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null, null);
        List<HomeVisitServiceDataModel> homeVisitList = getAllHomeVisitService(cursor);
        return homeVisitList;
    }

    public List<HomeVisitServiceDataModel> getLatestThreeEntry(String eventType) {
        SQLiteDatabase database = getReadableDatabase();
        String selection = EVENT_TYPE + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{eventType};
        net.sqlcipher.Cursor cursor = database.query(HOME_VISIT_SERVICE_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, DATE + " DESC", "3");
        List<HomeVisitServiceDataModel> homeVisitList = getAllHomeVisitService(cursor);
        return homeVisitList;
    }

    public List<HomeVisitServiceDataModel> getAllHomeVisitService(Cursor cursor) {
        List<HomeVisitServiceDataModel> homeVisitServiceDataModels = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    HomeVisitServiceDataModel homeVisitServiceDataModel = new HomeVisitServiceDataModel();
                    homeVisitServiceDataModel.setEventType(cursor.getString(cursor.getColumnIndex(EVENT_TYPE)));
                    homeVisitServiceDataModel.setHomeVisitDate(new Date(cursor.getString(cursor.getColumnIndex(DATE))));
                    homeVisitServiceDataModel.setHomeVisitDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
                    homeVisitServiceDataModel.setHomeVisitId(cursor.getString(cursor.getColumnIndex(HOME_VISIT_ID)));
                    //duplicate handle
                    if (homeVisitServiceDataModel.getHomeVisitId() != null && !isExist(homeVisitServiceDataModels, homeVisitServiceDataModel.getEventType(), homeVisitServiceDataModel.getHomeVisitId())) {
                        homeVisitServiceDataModels.add(homeVisitServiceDataModel);
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return homeVisitServiceDataModels;

    }

    private boolean isExist(List<HomeVisitServiceDataModel> homeVisitServiceDataModels, String type, String homeVisitId) {
        for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitServiceDataModels) {
            if (homeVisitServiceDataModel.getEventType().equalsIgnoreCase(type)
                    && homeVisitServiceDataModel.getHomeVisitId().equalsIgnoreCase(homeVisitId)) {
                return true;
            }
        }
        return false;
    }

    public void update(SQLiteDatabase database, HomeVisitServiceDataModel homeVisitServiceDataModel) {
        if (homeVisitServiceDataModel == null || homeVisitServiceDataModel.getHomeVisitId() == null) {
            return;
        }

        try {
            String idSelection = HOME_VISIT_ID + " = ? and " + EVENT_TYPE + " = ?";
            database.update(HOME_VISIT_SERVICE_TABLE_NAME, createValuesFor(homeVisitServiceDataModel), idSelection, new String[]{homeVisitServiceDataModel.getHomeVisitId(), homeVisitServiceDataModel.getEventType()});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private ContentValues createValuesFor(HomeVisitServiceDataModel homeVisitServiceDataModel) {
        ContentValues values = new ContentValues();
        values.put(HOME_VISIT_ID, homeVisitServiceDataModel.getHomeVisitId());
        values.put(DETAILS, homeVisitServiceDataModel.getHomeVisitDetails());
        values.put(EVENT_TYPE, homeVisitServiceDataModel.getEventType());
        values.put(DATE, homeVisitServiceDataModel.getHomeVisitDate() + "");

        return values;
    }


}
