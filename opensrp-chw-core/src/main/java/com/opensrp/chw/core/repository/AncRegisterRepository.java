package com.opensrp.chw.core.repository;

import com.opensrp.chw.core.utils.CoreConstants;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.anc.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.HashMap;

public class AncRegisterRepository extends BaseRepository {

    public static final String TABLE_NAME = "ec_family_member";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String LAST_MENSTRUAL_PERIOD = "last_menstrual_period";

    public static final String[] TABLE_COLUMNS = {FIRST_NAME, MIDDLE_NAME, LAST_NAME, PHONE_NUMBER};
    public static final String[] ANC_COUNT_TABLE_COLUMNS = {BASE_ENTITY_ID};
    public static final String[] LAST_MENSTRUAL_PERIOD_COLUMNS = {LAST_MENSTRUAL_PERIOD};


    public AncRegisterRepository(Repository repository) {
        super(repository);
    }

    public HashMap<String, String> getFamilyNameAndPhone(String baseEntityID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = null;
        try {
            if (database == null) {
                return null;
            }
            String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE;
            String[] selectionArgs = new String[]{baseEntityID};

            cursor = database.query(TABLE_NAME, TABLE_COLUMNS, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                HashMap<String, String> detailsMap = new HashMap<>();
                String name = org.smartregister.util.Utils.getName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndex(MIDDLE_NAME)));
                if (cursor.getString(cursor.getColumnIndex(LAST_NAME)) != null) {
                    name = org.smartregister.util.Utils.getName(name, cursor.getString(cursor.getColumnIndex(LAST_NAME)));
                }
                detailsMap.put(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, name);
                detailsMap.put(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)));

                return detailsMap;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }

    public String getGaIfAncWoman(String baseEntityID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = null;
        try {
            if (database == null) {
                return null;
            }
            String selection = DBConstants.KEY.BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " +
                    org.smartregister.chw.anc.util.DBConstants.KEY.IS_CLOSED + " = ? " + COLLATE_NOCASE;
            String[] selectionArgs = new String[]{baseEntityID, "0"};


            cursor = database.query(CoreConstants.TABLE_NAME.ANC_MEMBER, LAST_MENSTRUAL_PERIOD_COLUMNS, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(LAST_MENSTRUAL_PERIOD));
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }

    public int getAncWomenCount(String familyBaseID) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = null;
        try {
            if (database == null) {
                return 0;
            }
            String selection = DBConstants.KEY.RELATIONAL_ID + " = ? " + COLLATE_NOCASE + " AND " +
                    org.smartregister.chw.anc.util.DBConstants.KEY.IS_CLOSED + " = ? " + COLLATE_NOCASE;
            String[] selectionArgs = new String[]{familyBaseID, "0"};

            cursor = database.query(CoreConstants.TABLE_NAME.ANC_MEMBER,
                    ANC_COUNT_TABLE_COLUMNS, selection, selectionArgs, null, null, null);

            return cursor.getCount();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;

    }
}
