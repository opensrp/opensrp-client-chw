package org.smartregister.chw.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;
import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeVisitServiceRepository extends BaseRepository {
    private static final String TAG = HomeVisitServiceRepository.class.getCanonicalName();
    private static final String HOME_VISIT_SERVICE_SQL = "CREATE TABLE home_visit_service (home_visit_id VARCHAR NOT NULL,event_type VARCHAR,details TEXT,date DATETIME NOT NULL)";
    public static final String HOME_VISIT_SERVICE_TABLE_NAME = "home_visit_service";
    public static final String HOME_VISIT_ID = "home_visit_id";
    public static final String EVENT_TYPE = "event_type";
    public static final String DATE = "date";
    public static final String DETAILS = "details";

    public static final String[] TABLE_COLUMNS = {HOME_VISIT_ID, EVENT_TYPE, DATE,DETAILS};
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            if(homeVisitServiceDataModel.getHomeVisitId() != null){
                HomeVisitServiceDataModel uniqueVisit  = findUnique(database,homeVisitServiceDataModel);
                if(uniqueVisit!=null){
                    update(database,homeVisitServiceDataModel);
                }else{
                    database.insert(HOME_VISIT_SERVICE_TABLE_NAME, null, createValuesFor(homeVisitServiceDataModel));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    public HomeVisitServiceDataModel findUnique(SQLiteDatabase database,HomeVisitServiceDataModel homeVisitServiceDataModel){
        if(homeVisitServiceDataModel == null || TextUtils.isEmpty(homeVisitServiceDataModel.getHomeVisitId())){
            return null;
        }
        if (database == null) {
            database = getReadableDatabase();
        }
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(homeVisitServiceDataModel.getHomeVisitId())) {
            selection = HOME_VISIT_ID + " = ? " + COLLATE_NOCASE+" and "+EVENT_TYPE+" = ?";
            selectionArgs = new String[]{homeVisitServiceDataModel.getHomeVisitId(),homeVisitServiceDataModel.getEventType()};
        }
        net.sqlcipher.Cursor cursor = database.query(HOME_VISIT_SERVICE_TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null, null);
        List<HomeVisitServiceDataModel> homeVisitList = getAllHomeVisitService(cursor);
        if (!homeVisitList.isEmpty()) {
            return homeVisitList.get(0);
        }
        return null;
    }
    public List<HomeVisitServiceDataModel> getAllHomeVisitService(Cursor cursor){
        List<HomeVisitServiceDataModel> homeVisitServiceDataModels = new ArrayList<>();
        try{
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    HomeVisitServiceDataModel homeVisitServiceDataModel = new HomeVisitServiceDataModel();
                    homeVisitServiceDataModel.setEventType(cursor.getString(cursor.getColumnIndex(EVENT_TYPE)));
                    homeVisitServiceDataModel.setHomeVisitDate(new Date(cursor.getString(cursor.getColumnIndex(DATE))));
                    homeVisitServiceDataModel.setHomeVisitDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
                    homeVisitServiceDataModel.setHomeVisitId(cursor.getString(cursor.getColumnIndex(HOME_VISIT_ID)));
                    homeVisitServiceDataModels.add(homeVisitServiceDataModel);
                    cursor.moveToNext();
                }
            }
        }catch (Exception e){

        }
        finally {
            cursor.close();
        }
        return homeVisitServiceDataModels;

    }
    public void update(SQLiteDatabase database, HomeVisitServiceDataModel homeVisitServiceDataModel) {
        if (homeVisitServiceDataModel == null || homeVisitServiceDataModel.getHomeVisitId() == null) {
            return;
        }

        try {
            String idSelection = HOME_VISIT_ID + " = ?";
            database.update(HOME_VISIT_SERVICE_TABLE_NAME, createValuesFor(homeVisitServiceDataModel), idSelection, new String[]{homeVisitServiceDataModel.getHomeVisitId()});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private ContentValues createValuesFor(HomeVisitServiceDataModel homeVisitServiceDataModel) {
        ContentValues values = new ContentValues();
        values.put(HOME_VISIT_ID, homeVisitServiceDataModel.getHomeVisitId());
        values.put(DETAILS, homeVisitServiceDataModel.getHomeVisitDetails());
        values.put(EVENT_TYPE, homeVisitServiceDataModel.getEventType());
        values.put(DATE, dateFormat.format(homeVisitServiceDataModel.getHomeVisitDate()));
        return values;
    }




}
