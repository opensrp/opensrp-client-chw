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

    public static String getChildGender(String baseEntityID) {
        String sql = String.format("SELECT gender from ec_child\n" +
                "where base_entity_id = '%s'", baseEntityID);

        DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return "";

        return values.get(0) == null ? "" : values.get(0); // Return a default value of Low
    }


}
