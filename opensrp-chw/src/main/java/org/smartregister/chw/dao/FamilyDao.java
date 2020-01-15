package org.smartregister.chw.dao;

import android.util.Pair;

import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.AlertStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyDao extends AbstractDao {

    public static Map<String, Integer> getFamilyServiceSchedule(String familyBaseEntityID) {
        String sql = "select visit_state , count(*) totals  from (  " +
                "SELECT s.base_entity_id , m.relational_id , CASE  " +
                "WHEN completion_date  is NOT NULL  THEN  'DONE'  " +
                "WHEN not_done_date is NOT NULL THEN  'NOT_VISIT_THIS_MONTH' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN  'DUE' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN  'OVERDUE' " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  'EXPIRY'  end  visit_state " +
                "FROM schedule_service s " +
                "LEFT join ec_family_member m on s.base_entity_id  = m.base_entity_id COLLATE NOCASE " +
                "WHERE visit_state is NOT NULL " +
                ") counters   where " +
                " ((counters.relational_id = '" + familyBaseEntityID + "' COLLATE NOCASE) or " +
                " (counters.base_entity_id = '" + familyBaseEntityID + "' COLLATE NOCASE)) " +
                "group by visit_state";


        DataMap<Pair<String, Integer>> dataMap = c -> Pair.create(getCursorValue(c, "visit_state"), getCursorIntValue(c, "totals"));

        Map<String, Integer> visits = new HashMap<>();
        List<Pair<String, Integer>> pairs = AbstractDao.readData(sql, dataMap);
        if (pairs == null || pairs.size() == 0)
            return visits;

        for (Pair<String, Integer> pair : pairs) {
            visits.put(pair.first, pair.second);
        }

        return visits;
    }

    public static String getMemberDueStatus(String memberBaseEntityId) {
        String sql = "select visit_state from (  " +
                "SELECT m.relational_id , CASE  " +
                "WHEN completion_date  is NOT NULL  THEN  'DONE'  " +
                "WHEN not_done_date is NOT NULL AND not_done_date >= due_date THEN  'NOT_VISIT_THIS_MONTH' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN  'DUE' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN  'OVERDUE' " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  'EXPIRY'  end  visit_state " +
                "FROM schedule_service s " +
                "inner join ec_family_member m on s.base_entity_id  ='" + memberBaseEntityId + "'" + " COLLATE NOCASE " +
                "WHERE visit_state is NOT NULL " +
                ") counters " +
                "group by visit_state";

        DataMap<String> dataMap = c -> getCursorValue(c, "visit_state");
        String dueStatus;
        List<String> dueStatusListString = AbstractDao.readData(sql, dataMap);
        if (dueStatusListString.size() > 0) {
            dueStatus = dueStatusListString.get(0);
        } else {
            dueStatus = "";
        }

        return dueStatus;

    }

    public static long getFamilyCreateDate(String familyBaseEntityID) {
        String sql = "select eventDate from event where eventType = 'Family Registration' and " +
                "baseEntityId = '" + familyBaseEntityID + "' order by eventDate desc limit 1";

        DataMap<Date> dataMap = c -> getCursorValueAsDate(c, "eventDate", getDobDateFormat());
        List<Date> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;

        return res.get(0).getTime();
    }

    public static boolean isFamily(String baseEntityID) {
        String sql = "select count(*) count from ec_family where base_entity_id = '" + baseEntityID + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    // checks if the
    public static AlertStatus getFamilyAlertStatus(String baseEntityID) {
        String sql = "select max(case when over_due_date <= date('now') then 2 else 1 end) status " +
                "from schedule_service where (base_entity_id = '" + baseEntityID + "' " +
                "or base_entity_id in (select base_entity_id from ec_family_member where relational_id = '" + baseEntityID + "')) " +
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