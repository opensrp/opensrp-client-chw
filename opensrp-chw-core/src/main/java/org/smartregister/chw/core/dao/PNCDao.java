package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PNCDao extends AlertDao {

    @Nullable
    public static String getPNCDeliveryDate(String baseEntityID) {
        String sql = "select delivery_date from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = cursor -> getCursorValue(cursor, "date_created");

        List<String> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }
}
