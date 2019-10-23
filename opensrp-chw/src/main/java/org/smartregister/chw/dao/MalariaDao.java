package org.smartregister.chw.dao;

import java.util.List;

public class MalariaDao extends org.smartregister.chw.core.dao.MalariaDao {

    public static boolean isRegisteredForMalaria(String baseEntityID) {
        String sql = String.format(
                "select count(ec_malaria_confirmation.base_entity_id) count\n" +
                        "from ec_malaria_confirmation\n" +
                        "where base_entity_id = '%s'\n" +
                        "  and ec_malaria_confirmation.is_closed = 0\n" +
                        "  and ec_malaria_confirmation.malaria_test_date IS NOT NULL", baseEntityID);

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }
}
