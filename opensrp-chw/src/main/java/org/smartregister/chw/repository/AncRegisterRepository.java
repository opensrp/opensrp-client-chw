package org.smartregister.chw.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.AlertService;

public class AncRegisterRepository extends BaseRepository {

    public static final String TABLE_NAME = "ec_family_member";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String BASE_ENTITY_ID = "base_entity_id";

    public static final String[] TABLE_COLUMNS = {FIRST_NAME, MIDDLE_NAME, LAST_NAME};


    private CommonFtsObject commonFtsObject;
    private AlertService alertService;

    public AncRegisterRepository(Repository repository, CommonFtsObject commonFtsObject, AlertService alertService) {
        super(repository);
        this.commonFtsObject = commonFtsObject;
        this.alertService = alertService;
    }

    public String familyHeadName(String baseEntityID) {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = null;
        try {
            if (database == null) {
                database = getReadableDatabase();
            }
            String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE;
            String[] selectionArgs = new String[]{baseEntityID};

            cursor = database.query(TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String name = org.smartregister.util.Utils.getName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndex(MIDDLE_NAME)));
                if (cursor.getString(cursor.getColumnIndex(LAST_NAME)) != null) {
                    name = org.smartregister.util.Utils.getName(name, cursor.getString(cursor.getColumnIndex(LAST_NAME)));
                }
                return name;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }
}
