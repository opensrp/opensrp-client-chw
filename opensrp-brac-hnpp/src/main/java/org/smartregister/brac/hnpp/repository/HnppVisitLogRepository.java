package org.smartregister.brac.hnpp.repository;

import android.content.ContentValues;
import android.text.TextUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import org.smartregister.brac.hnpp.utils.VisitLog;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import java.util.ArrayList;
import java.util.List;

public class HnppVisitLogRepository extends BaseRepository {

    public static final String VISIT_LOG_TABLE_NAME = "ec_visit_log";
    public static final String VISIT_ID = "visit_id";
    public static final String VISIT_TYPE = "visit_type";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String VISIT_DATE = "visit_date";
    public static final String EVENT_TYPE = "event_type";
    public static final String VISIT_JSON = "visit_json";
    public static final String[] TABLE_COLUMNS = {VISIT_ID, VISIT_TYPE, BASE_ENTITY_ID, VISIT_DATE,EVENT_TYPE,VISIT_JSON};
    private static final String VISIT_LOG_SQL = "CREATE TABLE ec_visit_log (visit_id VARCHAR,visit_type VARCHAR,base_entity_id VARCHAR NOT NULL,visit_date VARCHAR,event_type VARCHAR,visit_json TEXT)";

    public HnppVisitLogRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(VISIT_LOG_SQL);
    }

    public void add(VisitLog visitLog) {
        if (visitLog == null) {
            return;
        }
        try {
            SQLiteDatabase database = getWritableDatabase();

            if (visitLog.getBaseEntityId() != null && findUnique(database, visitLog) == null) {
            database.insert(VISIT_LOG_TABLE_NAME, null, createValuesFor(visitLog));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public ArrayList<String> getVisitIds(){
        ArrayList<String>visit_ids = new ArrayList<String>();
        try{

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("select visits.visit_id from visits where visits.visit_id NOT IN (select ec_visit_log.visit_id from ec_visit_log) order by visit_date DESC",null);
            if(cursor!=null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String visit_id = cursor.getString(0);
                    visit_ids.add(visit_id);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }catch(Exception e){

        }
        return visit_ids;
    }
    public VisitLog findUnique(SQLiteDatabase db, VisitLog visitLog) {
        if (visitLog == null || TextUtils.isEmpty(visitLog.getBaseEntityId())) {
            return null;
        }
        SQLiteDatabase database = (db == null) ? getReadableDatabase() : db;
        String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " and " + VISIT_DATE + " = ? " + COLLATE_NOCASE+" and "+EVENT_TYPE+" = ?"+COLLATE_NOCASE;
        String[] selectionArgs = new String[]{visitLog.getBaseEntityId(), visitLog.getVisitDate() + "",visitLog.getEventType()};
        net.sqlcipher.Cursor cursor = database.query(VISIT_LOG_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null, null);
        List<VisitLog> homeVisitList = getAllVisitLog(cursor);
        if (homeVisitList.size() > 0) {
            return homeVisitList.get(0);
        }
        return null;
    }

    private ContentValues createValuesFor(VisitLog visitLog) {
        ContentValues values = new ContentValues();
        values.put(VISIT_ID, visitLog.getVisitId());
        values.put(VISIT_TYPE, visitLog.getVisitType());
        values.put(BASE_ENTITY_ID, visitLog.getBaseEntityId());
        values.put(VISIT_DATE, visitLog.getVisitDate());
        values.put(EVENT_TYPE, visitLog.getEventType());
        values.put(VISIT_JSON, visitLog.getVisitJson());
        return values;
    }

    private ArrayList<VisitLog> getAllVisitLog(Cursor cursor) {
        ArrayList<VisitLog> visitLogs = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    VisitLog visitLog = new VisitLog();
                    visitLog.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
                    visitLog.setEventType(cursor.getString(cursor.getColumnIndex(EVENT_TYPE)));
                    visitLog.setVisitDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(VISIT_DATE))));
                    visitLog.setVisitJson(cursor.getString(cursor.getColumnIndex(VISIT_JSON)));
                    visitLog.setVisitType(cursor.getString(cursor.getColumnIndex(VISIT_TYPE)));
                    visitLogs.add(visitLog);
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
        return visitLogs;

    }

    public ArrayList<VisitLog> getAllVisitLog(String baseEntityId) {
        SQLiteDatabase database = getReadableDatabase();
        String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{baseEntityId};
        net.sqlcipher.Cursor cursor = database.query(VISIT_LOG_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, VISIT_DATE + " DESC");
        return getAllVisitLog(cursor);
    }

    public VisitLog getLatestEntry(String familyId) {
        SQLiteDatabase database = getReadableDatabase();

        return null;
    }



}
