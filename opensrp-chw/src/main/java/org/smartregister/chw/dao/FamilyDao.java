package org.smartregister.chw.dao;

import android.util.Pair;

import org.smartregister.chw.core.dao.AbstractDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FamilyDao extends AbstractDao {

    public static Map<String, Integer> getFamilyServiceSchedule(String familyBaseEntityID) {
        String sql = "select counters.relational_id , visit_state , count(*) totals  from (  " +
                "SELECT m.relational_id , CASE  " +
                "WHEN completion_date  is NOT NULL  THEN  'DONE'  " +
                "WHEN not_done_date is NOT NULL THEN  'NOT_VISIT_THIS_MONTH' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN  'DUE' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN  'OVERDUE' " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  'EXPIRY'  end  visit_state " +
                "FROM schedule_service s " +
                "inner join ec_family_member m on s.base_entity_id  = m.base_entity_id " +
                "WHERE visit_state is NOT NULL " +
                ") counters   where counters.relational_id = '" + familyBaseEntityID + "' " +
                "group by relational_id, visit_state";


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
