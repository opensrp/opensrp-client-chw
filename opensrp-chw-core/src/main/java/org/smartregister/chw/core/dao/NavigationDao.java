package org.smartregister.chw.core.dao;

import android.database.Cursor;

import java.util.List;

public class NavigationDao extends AbstractDao {

    public static Integer getQueryCount(String sql) {

        DataMap<Integer> dataMap = c -> c.getType(0) == Cursor.FIELD_TYPE_NULL ? null : c.getInt(0);
        List<Integer> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0 || values.get(0) == null)
            return 0;

        return values.get(0);
    }

    public static Integer getTableCount(String table_name) {
        String sql = "select count(*) count from " + table_name;
        return getQueryCount(sql);
    }
}
