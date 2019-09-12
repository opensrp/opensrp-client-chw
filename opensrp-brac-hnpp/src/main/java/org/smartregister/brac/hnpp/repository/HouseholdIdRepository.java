package org.smartregister.brac.hnpp.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.domain.HouseholdId;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.ReportRepository;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HouseholdIdRepository extends BaseRepository {
    private static final String TAG = HouseholdIdRepository.class.getCanonicalName();
    private static final String HouseholdIds_SQL = "CREATE TABLE household_ids(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,village_id VARCHAR NOT NULL,openmrs_id VARCHAR NOT NULL,status VARCHAR NULL, used_by VARCHAR NULL,synced_by VARCHAR NULL,created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP )";
    private static final String HouseholdIds_TABLE_NAME = "household_ids";
    private static final String ID_COLUMN = "_id";
    private static final String VILLAGE_ID_COLUMN = "village_id";
    private static final String OPENMRS_ID_COLUMN = "openmrs_id";
    private static final String STATUS_COLUMN = "status";
    private static final String USED_BY_COLUMN = "used_by";
    private static final String SYNCED_BY_COLUMN = "synced_by";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String[] HouseholdIds_TABLE_COLUMNS = {ID_COLUMN, VILLAGE_ID_COLUMN, OPENMRS_ID_COLUMN, STATUS_COLUMN, USED_BY_COLUMN, SYNCED_BY_COLUMN, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};

    private static final String STATUS_USED = "used";
    private static final String STATUS_NOT_USED = "not_used";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public HouseholdIdRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(HouseholdIds_SQL);
    }

    public void add(HouseholdId householdId) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.insert(HouseholdIds_TABLE_NAME, null, createValuesFor(householdId));
            //database.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * inserts ids in bulk to the db in a transaction since normally, each time db.insert() is used, SQLite creates a transaction (and resulting journal file in the filesystem), which slows things down.
     *
     * @param ids
     */
    public void bulkInserOpenmrsIds(List<HouseholdId> ids) {
        SQLiteDatabase database = getWritableDatabase();

        try {
            String userName = HnppApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();

            database.beginTransaction();
            for (HouseholdId id : ids) {
                ContentValues values = new ContentValues();
                values.put(VILLAGE_ID_COLUMN, id.getVillageId());
                values.put(OPENMRS_ID_COLUMN, id.getOpenmrsId());
                values.put(STATUS_COLUMN, STATUS_NOT_USED);
                values.put(SYNCED_BY_COLUMN, userName);
                values.put(CREATED_AT_COLUMN, dateFormat.format(new Date()));
                database.insert(HouseholdIds_TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }

    public Long countUnUsedIds() {
        long count = 0;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT COUNT (*) FROM " + HouseholdIds_TABLE_NAME + " WHERE " + STATUS_COLUMN + "=?",
                    new String[]{String.valueOf(STATUS_NOT_USED)});
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    public String openmrsIdsByVillage(String village_id) {
        String openmrs_ids = "";
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT " + OPENMRS_ID_COLUMN + " FROM " + HouseholdIds_TABLE_NAME + " WHERE " + STATUS_COLUMN + "=? AND" + VILLAGE_ID_COLUMN + " =? ORDER BY " + CREATED_AT_COLUMN + " ASC LIMIT 1",
                    new String[]{String.valueOf(STATUS_NOT_USED), String.valueOf(village_id)});
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                openmrs_ids = cursor.getString(0);
            }

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return openmrs_ids;
    }

    public String getUnusedVillageId(){
        String vid = "0";
        String ids = "";
        Cursor cursor = null;
        try{
            cursor = getWritableDatabase().
                    rawQuery("select "+VILLAGE_ID_COLUMN+" , count(*) as uncount from " +
                            HouseholdIds_TABLE_NAME + " where "+STATUS_COLUMN+" = 'not_used' group by "+VILLAGE_ID_COLUMN,null);

            if(cursor!=null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (cursor.getCount() > 0 && !cursor.isAfterLast()) {
                    vid = cursor.getString(0);
                    int vid_count = cursor.getInt(1);
                    if(vid_count<10){
                        ids = ids + vid + ",";
                    }
                }
                if(!ids.isEmpty()){
                    ids = ids.substring(0,ids.length()-1);
                    return ids;
                }else{
                    return "-1";
                }

            }else{
                return vid;
            }
        }catch(SQLException e){

        }
        return vid;
    }
    public Long countUnUsedIdsByVillage(String village_id) {
        long count = 0;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT " + OPENMRS_ID_COLUMN + " FROM " + HouseholdIds_TABLE_NAME + " WHERE " + STATUS_COLUMN + "=? AND" + VILLAGE_ID_COLUMN + " =? ORDER BY " + CREATED_AT_COLUMN + " ASC LIMIT 1",
                    new String[]{String.valueOf(STATUS_NOT_USED), String.valueOf(village_id)});
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    /**
     * get next available household id
     *
     * @return
     */
    public HouseholdId getNextHouseholdId(String village_id) {
        HouseholdId householdId = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(HouseholdIds_TABLE_NAME, HouseholdIds_TABLE_COLUMNS, STATUS_COLUMN + " = ?", new String[]{STATUS_NOT_USED}, null, null, CREATED_AT_COLUMN + " ASC", "1");
            List<HouseholdId> ids = readAll(cursor);
            householdId = ids.isEmpty() ? null : ids.get(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return householdId;
    }

    /**
     * mark and openmrsid as used
     *
     * @param openmrsId
     */
    public void close(String village_id, String openmrsId) {
        try {
            String id;
            String userName = HnppApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            if (openmrsId.contains("-")) {
                id = formatId(openmrsId);
            } else {
                id = openmrsId;
            }
            ContentValues values = new ContentValues();
            values.put(STATUS_COLUMN, STATUS_USED);
            values.put(USED_BY_COLUMN, userName);
            getWritableDatabase().update(HouseholdIds_TABLE_NAME, values, OPENMRS_ID_COLUMN + " = ? AND "+VILLAGE_ID_COLUMN+" = ?", new String[]{id,village_id});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * mark and openmrsid as NOT used
     *
     * @param openmrsId
     */
    public void open(String village_id, String openmrsId) {
        try {

            String openmrsId_ = !openmrsId.contains("-") ? formatId(openmrsId) : openmrsId;

            ContentValues values = new ContentValues();
            values.put(STATUS_COLUMN, STATUS_NOT_USED);
            values.put(USED_BY_COLUMN, "");
            getWritableDatabase().update(HouseholdIds_TABLE_NAME, values, OPENMRS_ID_COLUMN + " = ? AND "+VILLAGE_ID_COLUMN+" =?", new String[]{openmrsId_,village_id});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String formatId(String openmrsId) {
        int lastIndex = openmrsId.length() - 1;
        String tail = openmrsId.substring(lastIndex);
        return openmrsId.substring(0, lastIndex) + "-" + tail;
    }

    private ContentValues createValuesFor(HouseholdId householdId) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, householdId.getId());
        values.put(VILLAGE_ID_COLUMN, householdId.getVillageId());
        values.put(OPENMRS_ID_COLUMN, householdId.getOpenmrsId());
        values.put(STATUS_COLUMN, householdId.getStatus());
        values.put(USED_BY_COLUMN, householdId.getUsedBy());
        values.put(CREATED_AT_COLUMN, dateFormat.format(householdId.getCreatedAt()));
        return values;
    }

    private List<HouseholdId> readAll(Cursor cursor) {
        List<HouseholdId> HouseholdIds = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (cursor.getCount() > 0 && !cursor.isAfterLast()) {

                HouseholdIds.add(new HouseholdId(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), new Date(cursor.getLong(5))));

                cursor.moveToNext();
            }
        }
        return HouseholdIds;
    }


}

