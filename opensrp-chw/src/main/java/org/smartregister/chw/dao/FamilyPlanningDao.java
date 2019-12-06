package org.smartregister.chw.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.domain.FamilyPlanning;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class FamilyPlanningDao extends AbstractDao {

    @Nullable
    public static List<FamilyPlanning> getFamilyPlanningDetails(String baseEntityID) {
        String sql = "select fp_method_accepted, no_pillcycles, fp_reg_date from ec_family_planning where base_entity_id = '" + baseEntityID + "'" +
                "and is_closed is 0 and ecp = 1";

        DataMap<FamilyPlanning> dataMap = c -> new FamilyPlanning(
                getCursorValue(c, "fp_method_accepted"),
                getCursorIntValue(c, "no_pillcycles"),
                getCursorValueAsDate(c, "fp_reg_date", getNativeFormsDateFormat())
        );
        List<FamilyPlanning> familyPlannings = readData(sql, dataMap);
        if (familyPlannings.size() == 0) {
            return null;
        }
        return familyPlannings;
    }
}
