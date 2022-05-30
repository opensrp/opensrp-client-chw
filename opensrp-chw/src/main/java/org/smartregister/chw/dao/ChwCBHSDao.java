package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChwCBHSDao extends AbstractDao {
    public static boolean tbStatusAfterTestingDone(String baseEntityID) {
        String sql = "Select client_tb_status_after_testing from ec_cbhs_register where base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "client_tb_status_after_testing");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null)
            return !res.get(0).equalsIgnoreCase("unknown");
        return false;
    }
}
