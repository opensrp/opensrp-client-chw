package org.smartregister.chw.dao;

import org.smartregister.chw.core.dao.AncDao;

import java.util.List;

public class ChwAncDao extends AncDao {

    public static boolean isClientHighRisk(String baseEntityId) {
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "medical_surgical_history");

        String sql = String.format(
                "SELECT medical_surgical_history FROM %s WHERE base_entity_id = '%s' " +
                        "AND medical_surgical_history is not null ",
                "ec_anc_hf_data",
                baseEntityId
        );

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return !res.get(0).equalsIgnoreCase("none");
        } else {
            return false;
        }
    }
}
