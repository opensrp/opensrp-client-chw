package org.smartregister.brac.hnpp.repository;

import android.content.ContentValues;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import org.smartregister.brac.hnpp.utils.VisitLog;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import java.util.ArrayList;

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

//            if (washCheck.getFamilyBaseEntityId() != null && findUnique(database, washCheck) == null) {
            database.insert(VISIT_LOG_TABLE_NAME, null, createValuesFor(visitLog));
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public VisitLog findUnique(SQLiteDatabase db, VisitLog visitLog) {

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

        return visitLogs;

    }

    public ArrayList<VisitLog> getAllVisitLogTask(String familyId) {
        SQLiteDatabase database = getReadableDatabase();
       return getAllVisitLog(null);
    }

    public VisitLog getLatestEntry(String familyId) {
        SQLiteDatabase database = getReadableDatabase();

        return null;
    }



}
