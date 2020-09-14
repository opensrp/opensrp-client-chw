package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildDao extends AbstractDao {

    public static boolean hasDueVaccines(String baseEntityID) {
        String sql = "select count(*) count \n" +
                "FROM alerts  " +
                "where caseID = '" + baseEntityID + "'" +
                "and status = 'normal'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return false;

        res.size();
        return true;
    }
}
