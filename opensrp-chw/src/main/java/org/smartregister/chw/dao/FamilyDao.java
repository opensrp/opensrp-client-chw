package org.smartregister.chw.dao;

import android.util.Pair;

import org.smartregister.chw.core.dao.AbstractDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FamilyDao extends AbstractDao {

    public static Map<String, Integer> getFamilyServiceSchedule(String familyBaseEntityId) {
        String sql = "select m.relational_id , visit_state , count(*) totals " +
                "from ( " +
                "SELECT base_entity_id , CASE " +
                "WHEN completion_date is NOT NULL  AND completion_date >= due_date AND completion_date < expiry_date  THEN  visit_done  " +
                "WHEN not_done_date is NOT NULL " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN due " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN overdue " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  expiry end visit_state " +
                "FROM schedule_service " +
                ") counters  inner join ec_family_member m on counters.base_entity_id  = '" + familyBaseEntityId + "' " +
                "group by relational_id, visit_state ";


        DataMap<Pair<String, Integer>> dataMap = c -> Pair.create(getCursorValue(c, "visit_state"), getCursorIntValue(c, "totals"));

        Map<String, Integer> visits = new HashMap<>();
        List<Pair<String, Integer>> pairs = AbstractDao.readData(sql, dataMap);
        if(pairs == null || pairs.size() == 0)
            return visits;

        for (Pair<String, Integer> pair : pairs) {
            visits.put(pair.first, pair.second);
        }

        return visits;
    }


}
