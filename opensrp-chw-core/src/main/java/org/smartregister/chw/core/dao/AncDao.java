package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AncDao extends AlertDao {

    @Nullable
    public static String getAncDateCreated(String baseEntityID) {
        String sql = "select date_created from ec_anc_log where base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "date_created");

        List<String> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }
}
