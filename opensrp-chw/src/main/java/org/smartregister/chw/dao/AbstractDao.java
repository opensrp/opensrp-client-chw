package org.smartregister.chw.dao;

import android.database.Cursor;

import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class AbstractDao {

    private static SimpleDateFormat DOB_DATE_FORMAT;
    private static SimpleDateFormat NATIVE_FORMS_DATE_FORMAT;

    public static SimpleDateFormat getDobDateFormat() {
        if (DOB_DATE_FORMAT == null)
            DOB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return DOB_DATE_FORMAT;
    }

    public static SimpleDateFormat getNativeFormsDateFormat() {
        if (NATIVE_FORMS_DATE_FORMAT == null)
            NATIVE_FORMS_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        return NATIVE_FORMS_DATE_FORMAT;
    }

    /**
     * Returns a mapped pojo by reading the sqlite adapter
     * handles iteration and cursor disposable
     *
     * @param query
     * @param dataMap
     * @param <T>
     * @return
     */
    protected static <T> List<T> readData(String query, DataMap<T> dataMap) {

        try {
            CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.FAMILY);
            List<T> list = new ArrayList<>();

            Cursor cursor = commonRepository.queryTable(query);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(dataMap.readCursor(cursor));
                cursor.moveToNext();
            }
            return list;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    protected static String getCursorValue(Cursor c, String column_name) {
        return c.getType(c.getColumnIndex(column_name)) == Cursor.FIELD_TYPE_NULL ? null : c.getString(c.getColumnIndex(column_name));
    }

    public interface DataMap<T> {
        T readCursor(Cursor cursor);
    }
}
