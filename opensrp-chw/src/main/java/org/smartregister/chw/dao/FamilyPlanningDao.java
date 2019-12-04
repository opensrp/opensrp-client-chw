package org.smartregister.chw.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.dao.AbstractDao;

import java.util.Date;
import java.util.List;

public class FamilyPlanningDao extends AbstractDao {
    @Nullable
    public static Date getFamilyPlanningDate(String baseEntityID) {
        String sql = "select fp_reg_date from ec_family_planning where base_entity_id = '" + baseEntityID + "'";
        DataMap<Date> dataMap = (cursor) -> {
            return getCursorValueAsDate(cursor, "fp_reg_date", getNativeFormsDateFormat());
        };
        List<Date> res = readData(sql, dataMap);
        return res != null && res.size() == 1 ? (Date)res.get(0) : null;
    }

    @Nullable
    public static String getFamilyPlanningMethod(String baseEntityID) {
        String sql = "select fp_method_accepted from ec_family_planning where base_entity_id = '" + baseEntityID + "'";
        DataMap<String> dataMap = (cursor) -> {
            return getCursorValue(cursor, "fp_method_accepted");
        };
        List<String> res = readData(sql, dataMap);
        return res != null && res.size() == 1 ? (String)res.get(0) : null;
    }

    @Nullable
    public static Integer getFamilyPlanningPillCycles(String baseEntityID) {
        String sql = "select no_pillcycles from ec_family_planning where base_entity_id = '" + baseEntityID + "'";
        DataMap<Integer> dataMap = (cursor) -> {
            return getCursorIntValue(cursor,"no_pillcycles");
        };
        List<Integer> res = readData(sql, dataMap);
        return res != null && res.size() == 1 ? (Integer) res.get(0) : null;
    }
}
