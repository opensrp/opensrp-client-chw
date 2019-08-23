package org.smartregister.chw.core.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.core.utils.WashCheck;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class WashCheckRepository extends BaseRepository {
    public static final String WASH_CHECK_TABLE_NAME = "ec_wash_check_log";
    public static final String FAMILY_ID = "family_id";
    public static final String LAST_VISIT = "last_visit";
    public static final String DETAILS = "details_info";
    public static final String[] TABLE_COLUMNS = {FAMILY_ID, LAST_VISIT, DETAILS};
    private static final String WASH_CHECK_SQL = "CREATE TABLE ec_wash_check_log (family_id VARCHAR NOT NULL,last_visit VARCHAR,details_info TEXT)";

    public WashCheckRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(WASH_CHECK_SQL);
    }

    public void add(WashCheck washCheck) {
        if (washCheck == null) {
            return;
        }
        try {
            SQLiteDatabase database = getWritableDatabase();
            if (washCheck.getFamilyBaseEntityId() != null && findUnique(database, washCheck) == null) {
                database.insert(WASH_CHECK_TABLE_NAME, null, createValuesFor(washCheck));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public WashCheck findUnique(SQLiteDatabase db, WashCheck washCheck) {
        if (washCheck == null || TextUtils.isEmpty(washCheck.getFamilyBaseEntityId())) {
            return null;
        }
        SQLiteDatabase database = (db == null) ? getReadableDatabase() : db;
        String selection = FAMILY_ID + " = ? " + COLLATE_NOCASE + " and " + LAST_VISIT + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{washCheck.getFamilyBaseEntityId(), washCheck.getLastVisit() + ""};
        net.sqlcipher.Cursor cursor = database.query(WASH_CHECK_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null, null);
        List<WashCheck> homeVisitList = getAllWashCheck(cursor);
        if (homeVisitList.size() > 0) {
            return homeVisitList.get(0);
        }
        return null;
    }

    private ContentValues createValuesFor(WashCheck washCheck) {
        ContentValues values = new ContentValues();
        values.put(FAMILY_ID, washCheck.getFamilyBaseEntityId());
        values.put(DETAILS, washCheck.getDetailsJson());
        values.put(LAST_VISIT, washCheck.getLastVisit());
        return values;
    }

    private ArrayList<WashCheck> getAllWashCheck(Cursor cursor) {
        ArrayList<WashCheck> washChecks = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    WashCheck washCheck = new WashCheck();
                    washCheck.setFamilyBaseEntityId(cursor.getString(cursor.getColumnIndex(FAMILY_ID)));
                    washCheck.setLastVisit(Long.parseLong(cursor.getString(cursor.getColumnIndex(LAST_VISIT))));
                    washCheck.setDetailsJson(cursor.getString(cursor.getColumnIndex(DETAILS)));
                    washChecks.add(washCheck);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return washChecks;

    }

    public ArrayList<WashCheck> getAllWashCheckTask(String familyId) {
        SQLiteDatabase database = getReadableDatabase();
        String selection = FAMILY_ID + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{familyId};
        net.sqlcipher.Cursor cursor = database.query(WASH_CHECK_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, LAST_VISIT + " DESC");
        return getAllWashCheck(cursor);
    }

    public WashCheck getLatestEntry(String familyId) {
        SQLiteDatabase database = getReadableDatabase();
        String selection = FAMILY_ID + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{familyId};
        net.sqlcipher.Cursor cursor = database.query(WASH_CHECK_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, LAST_VISIT + " DESC", "1");
        ArrayList<WashCheck> washChecks = getAllWashCheck(cursor);
        if (washChecks.size() > 0) {
            return washChecks.get(0);
        }
        return null;
    }


}
