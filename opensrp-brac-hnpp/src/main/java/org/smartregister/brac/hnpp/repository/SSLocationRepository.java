package org.smartregister.brac.hnpp.repository;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.location.SSModel;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahmud on 11/23/18.
 */
public class SSLocationRepository extends BaseRepository {

    protected static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    protected static final String ID = "_id";
    protected static final String SS_NAME = "ss_name";
    protected static final String IS_SIMPRINT_ENABLE = "simprints_enable";
    protected static final String GEOJSON = "geojson";

    protected static final String LOCATION_TABLE = "ss_location";

    protected static final String[] COLUMNS = new String[]{ID, SS_NAME, GEOJSON};

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + LOCATION_TABLE + " (" +
                    ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    SS_NAME + " VARCHAR , " +IS_SIMPRINT_ENABLE + " VARCHAR , " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_NAME_INDEX = "CREATE INDEX "
            + LOCATION_TABLE + "_" + SS_NAME + "_ind ON " + LOCATION_TABLE + "(" + SS_NAME + ")";


    public SSLocationRepository(Repository repository) {
        super(repository);
    }

    protected String getLocationTableName() {
        return LOCATION_TABLE;
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_LOCATION_NAME_INDEX);
    }

    public void addOrUpdate(SSModel ssModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SS_NAME, ssModel.username);
        contentValues.put(IS_SIMPRINT_ENABLE, ssModel.simprints_enable);
        contentValues.put(GEOJSON, gson.toJson(ssModel.locations));
        if(isExistLocation(ssModel.username)){
            getWritableDatabase().update(getLocationTableName(),contentValues,SS_NAME+" =? ",new String[]{ssModel.username});
        }else{
            getWritableDatabase().replace(getLocationTableName(), null, contentValues);
        }


    }

    public ArrayList<SSModel> getAllLocations() {
        Cursor cursor = null;
        ArrayList<SSModel> locations = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName(), null);
            while (cursor.moveToNext()) {
                locations.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(LocationRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locations;
    }
    public boolean isExistLocation(String name) {
        Cursor cursor = null;
        ArrayList<String> locationIds = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT "+ID+" FROM " + getLocationTableName()+" where "+SS_NAME+" = '"+name+"'", null);
            while (cursor.moveToNext()) {
                locationIds.add(cursor.getString(0));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(LocationRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationIds.size()>0;
    }

    protected SSModel readCursor(Cursor cursor) {
        String geoJson = cursor.getString(cursor.getColumnIndex(GEOJSON));
        String name = cursor.getString(cursor.getColumnIndex(SS_NAME));
        String simprints = cursor.getString(cursor.getColumnIndex(IS_SIMPRINT_ENABLE));
        SSModel ssModel = new SSModel();
        ssModel.username = name;
        ssModel.simprints_enable = simprints.equalsIgnoreCase("1");
        try {
            JSONArray jsonArray = new JSONArray(geoJson);
            for(int i = 0; i <jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SSLocations locations = new Gson().fromJson(jsonObject.toString(), SSLocations.class);
                ssModel.locations.add(locations);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ssModel;
    }

}
