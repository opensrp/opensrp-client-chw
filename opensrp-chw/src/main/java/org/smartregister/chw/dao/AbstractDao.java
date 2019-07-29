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

    /*
        private Map<String, String> getChildDetails(String baseEntityId) {
            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), new String[]{CommonFtsObject.idColumn, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED});
            String query = queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' AND %s ",
                    DBConstants.KEY.DATE_REMOVED,
                    CommonFtsObject.idColumn,
                    baseEntityId,
                    ChildDBConstants.childAgeLimitFilter()));

            query = query.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD) + ".id as _id ,", "");

            CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.CHILD);
            List<Map<String, String>> res = new ArrayList<>();

            Cursor cursor = null;
            try {
                cursor = commonRepository.queryTable(query);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    int columncount = cursor.getColumnCount();
                    Map<String, String> columns = new HashMap<>();
                    for (int i = 0; i < columncount; i++) {
                        columns.put(cursor.getColumnName(i), cursor.getType(i) == Cursor.FIELD_TYPE_NULL ? null : String.valueOf(cursor.getString(i)));
                    }
                    res.add(columns);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                Timber.e(e, e.toString());
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            if (res.isEmpty()) {
                return null;
            }
            return res.get(0);
        }
    */
    public interface DataMap<T> {
        T readCursor(Cursor cursor);
    }
}
