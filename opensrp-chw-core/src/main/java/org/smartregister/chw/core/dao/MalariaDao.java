package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class MalariaDao extends AbstractDao {

    @Nullable
    public static Date getMalariaTestDate(String baseEntityID) {
        String sql = "select malaria_test_date from ec_malaria_confirmation where base_entity_id = '" + baseEntityID + "'";

        DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "malaria_test_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }
}
