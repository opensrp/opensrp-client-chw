package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChwPNCDaoFlv extends DefaultChwPNCDaoFlv {

    public static boolean hasFamilyPlanning(String baseEntityID) {
        String sql = "select count(*) records from visit_details vd  " +
                "inner join visits v on vd.visit_id = v.visit_id COLLATE NOCASE and vd.visit_key = 'fp_method' and vd.human_readable_details <> 'None' " +
                "where v.base_entity_id =  '" + baseEntityID + "' and v.processed = 1 ";

        AbstractDao.DataMap<Integer> dataMap = c -> getCursorIntValue(c, "records");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() < 1)
            return false;

        return res.get(0) > 0;
    }
}
