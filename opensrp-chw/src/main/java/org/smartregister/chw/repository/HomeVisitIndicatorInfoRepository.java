package org.smartregister.chw.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.domain.HomeVisitIndicatorInfo;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

public class HomeVisitIndicatorInfoRepository extends BaseRepository {

    private static final String TAG = HomeVisitIndicatorInfoRepository.class.getCanonicalName();

    private static final String HOME_VISIT_INDICATOR_INFO_TABLE = "home_visit_indicator_info";
    private static final String ID = "_id";
    private static final String HOME_VISIT_ID = "home_visit_id";
    private static final String SERVICE = "service";
    private static final String SERVICE_DATE = "service_date";
    private static final String STATUS = "status";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATE_HOME_VISIT_INDICATOR_INFO_TABLE = "CREATE TABLE " + HOME_VISIT_INDICATOR_INFO_TABLE + "(" + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + HOME_VISIT_ID + "INTEGER NOT NULL, " + SERVICE + " VARCHAR NOT NULL, " + SERVICE_DATE + " DATE, " + STATUS + " VARCHAR, " + BASE_ENTITY_ID + " VARCHAR NOT NULL, " + EVENT_TYPE +
            " VARCHAR NOT NULL, " + UPDATED_AT + " INTEGER)";


    public HomeVisitIndicatorInfoRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_HOME_VISIT_INDICATOR_INFO_TABLE);
    }

    public void addHomeVisitInfo(HomeVisitIndicatorInfo homeVisitIndicatorInfo) {
        if (homeVisitIndicatorInfo == null) {
            return;
        }
        getWritableDatabase().insert(HOME_VISIT_INDICATOR_INFO_TABLE, null, createValuesFor(homeVisitIndicatorInfo));
    }

    private ContentValues createValuesFor(HomeVisitIndicatorInfo homeVisitIndicatorInfo) {
        ContentValues values = new ContentValues();
        values.put(HOME_VISIT_ID, homeVisitIndicatorInfo.getHomeVisitId());
        values.put(SERVICE, homeVisitIndicatorInfo.getService());
        values.put(SERVICE_DATE, homeVisitIndicatorInfo.getServiceDate());
        values.put(STATUS, homeVisitIndicatorInfo.getStatus());
        values.put(BASE_ENTITY_ID, homeVisitIndicatorInfo.getBaseEntityId());
        values.put(EVENT_TYPE, homeVisitIndicatorInfo.getEventType());
        values.put(UPDATED_AT, homeVisitIndicatorInfo.getUpdatedAt());
        return values;
    }
}
