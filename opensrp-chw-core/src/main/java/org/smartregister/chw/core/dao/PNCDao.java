package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.core.model.ChildModel;

import java.util.Date;
import java.util.List;

public class PNCDao extends AbstractDao {

    @Nullable
    public static Date getPNCDeliveryDate(String baseEntityID) {
        String sql = "select delivery_date from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "delivery_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static boolean isPNCMember(String baseEntityID) {
        String sql = "select count(*) count from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "' and is_closed = 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static List<ChildModel> childrenForPncWoman(String baseEntityId) {
        String sql = String.format("select first_name || ' ' || middle_name || ' ' || last_name as child_name, dob " +
                "FROM ec_child WHERE mother_entity_id ='%s' AND  entry_point = '%s'", baseEntityId, "PNC");

        AbstractDao.DataMap<ChildModel> dataMap = cursor ->
                new ChildModel(getCursorValue(cursor, "child_name"), getCursorValue(cursor, "dob"));

        return readData(sql, dataMap);
    }

}
