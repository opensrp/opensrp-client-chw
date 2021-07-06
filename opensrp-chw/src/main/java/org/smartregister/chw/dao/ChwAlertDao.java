package org.smartregister.chw.dao;

import org.smartregister.chw.core.dao.AlertDao;
import org.smartregister.domain.AlertStatus;

import java.util.List;

public class ChwAlertDao extends AlertDao {

    public static AlertStatus getFamilyAlertStatus(String familyBaseEntityID, String childBaseEntityId) {
        String sql = "select max(case when over_due_date <= date('now') then 2 else 1 end) status " +
                "from schedule_service where (base_entity_id = '" + familyBaseEntityID + "' " +
                "or base_entity_id in (select base_entity_id from ec_family_member " +
                "where relational_id = '" + familyBaseEntityID + "' and base_entity_id <> '" + childBaseEntityId + "' )) " +
                "and due_date <= date('now') and expiry_date > date('now') and completion_date is null ";

        DataMap<Integer> dataMap = c -> getCursorIntValue(c, "status");
        List<Integer> readData = readData(sql, dataMap);

        if (readData == null || readData.size() == 0 || readData.get(0) == null)
            return AlertStatus.complete;

        if (readData.get(0).equals(2))
            return AlertStatus.urgent;

        return AlertStatus.normal;
    }
}
