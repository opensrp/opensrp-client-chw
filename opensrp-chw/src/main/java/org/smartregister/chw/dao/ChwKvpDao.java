package org.smartregister.chw.dao;

import org.smartregister.chw.kvp.dao.KvpDao;

import java.util.List;

public class ChwKvpDao extends KvpDao {
    public static String getDominantKVPGroup(String baseEntityId) {
        String sql = "SELECT client_group FROM ec_kvp_prep_register p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' AND p.is_closed = 0 ";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "client_group");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }
}
