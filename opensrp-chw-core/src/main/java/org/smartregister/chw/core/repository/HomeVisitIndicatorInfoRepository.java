package org.smartregister.chw.core.repository;

import android.content.ContentValues;

import org.smartregister.chw.core.domain.HomeVisitIndicatorInfo;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeVisitIndicatorInfoRepository extends BaseRepository {
    public static final String HOME_VISIT_INDICATOR_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String HOME_VISIT_INDICATOR_INFO_TABLE = "home_visit_indicator_info";
    private static final String ID = "_id";
    private static final String HOME_VISIT_ID = "home_visit_id";
    private static final String HOME_VISIT_DATE = "home_visit_date";
    private static final String SERVICE = "service";
    private static final String SERVICE_DATE = "service_date";
    private static final String SERVICE_UPDATE_DATE = "service_update_date";
    private static final String SERVICE_GIVEN = "service_given";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String VALUE = "value";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";
    private static final String CREATE_HOME_VISIT_INDICATOR_INFO_TABLE = "CREATE TABLE " + HOME_VISIT_INDICATOR_INFO_TABLE + "(" + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + HOME_VISIT_ID + " INTEGER NOT NULL, " + HOME_VISIT_DATE + " DATETIME NOT NULL, " + SERVICE + " VARCHAR NOT NULL, " + SERVICE_DATE + " DATETIME, " + SERVICE_UPDATE_DATE + " DATETIME, "
            + SERVICE_GIVEN + " BOOLEAN NOT NULL, " + BASE_ENTITY_ID + " VARCHAR NOT NULL, " + VALUE + " VARCHAR, " + UPDATED_AT + " DATETIME, " + CREATED_AT + " DATETIME NOT NULL)";
    private static String TAG = HomeVisitIndicatorInfoRepository.class.getCanonicalName();
    String dateFormat = HomeVisitIndicatorInfoRepository.HOME_VISIT_INDICATOR_DATE_FORMAT;

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
        SQLiteDatabase database = getWritableDatabase();
        // Handle updated home visit details
        String createdAtDateString = formatDate(homeVisitIndicatorInfo.getCreatedAt(), dateFormat);
        database.delete(HOME_VISIT_INDICATOR_INFO_TABLE, SERVICE + " = ? AND " +
                CREATED_AT + " = ? ", new String[]{homeVisitIndicatorInfo.getService(), createdAtDateString});
        database.insert(HOME_VISIT_INDICATOR_INFO_TABLE, null, createValuesFor(homeVisitIndicatorInfo));
    }

    private ContentValues createValuesFor(HomeVisitIndicatorInfo homeVisitIndicatorInfo) {
        ContentValues values = new ContentValues();
        values.put(HOME_VISIT_ID, homeVisitIndicatorInfo.getHomeVisitId());
        values.put(HOME_VISIT_DATE, formatDate(homeVisitIndicatorInfo.getLastHomeVisitDate(), dateFormat));
        values.put(SERVICE, homeVisitIndicatorInfo.getService());
        values.put(SERVICE_DATE, formatDate(homeVisitIndicatorInfo.getServiceDate(), dateFormat));
        values.put(SERVICE_UPDATE_DATE, formatDate(homeVisitIndicatorInfo.getServiceUpdateDate(), dateFormat));
        values.put(SERVICE_GIVEN, homeVisitIndicatorInfo.isServiceGiven());
        values.put(BASE_ENTITY_ID, homeVisitIndicatorInfo.getBaseEntityId());
        values.put(VALUE, homeVisitIndicatorInfo.getValue());
        values.put(UPDATED_AT, formatDate(homeVisitIndicatorInfo.getUpdatedAt(), dateFormat));
        values.put(CREATED_AT, formatDate(homeVisitIndicatorInfo.getCreatedAt(), dateFormat));
        return values;
    }

    private String formatDate(Date date, String format) {
        return (date != null ? new SimpleDateFormat(format, Locale.getDefault()).format(date) : "");
    }
}
