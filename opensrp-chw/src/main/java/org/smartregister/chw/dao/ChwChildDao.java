package org.smartregister.chw.dao;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.domain.Child;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChwChildDao extends ChildDao {

    public static boolean hasDueVaccines(String baseEntityID) {
        String sql = "select count(*) count \n" +
                "FROM alerts  " +
                "where caseID = '" + baseEntityID + "'" +
                "and status = 'normal'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return false;

        res.size();
        return true;
    }

    public static String getChildGender(String baseEntityID) {
        String sql = String.format("SELECT gender from ec_child\n" +
                "where base_entity_id = '%s'", baseEntityID);

        DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return "";

        return values.get(0) == null ? "" : values.get(0); // Return a default value of Low
    }


    public static String getChildQuery(String baseEntityID) {
        if (!ChwApplication.getApplicationFlavor().hasHpvVaccineChildren()) {
            return ChildDao.getChildQuery(baseEntityID);
        } else {
            return "select  c.base_entity_id , c.first_name , c.last_name , c.middle_name , c.mother_entity_id , c.relational_id , c.dob , c.date_created ,  lastVisit.last_visit_date , last_visit_not_done_date " +
                    "from ec_child c " +
                    "inner join ec_family_member m on c.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                    "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE  " +
                    "left join ( " +
                    " select base_entity_id , max(visit_date) last_visit_date " +
                    " from visits " +
                    " where visit_type in ('Child Home Visit') " +
                    " group by base_entity_id " +
                    ") lastVisit on lastVisit.base_entity_id = c.base_entity_id " +
                    "left join ( " +
                    " select base_entity_id , max(visit_date) last_visit_not_done_date " +
                    " from visits " +
                    " where visit_type in ('Visit not done') " +
                    " group by base_entity_id " +
                    ") lastVisitNotDone on lastVisitNotDone.base_entity_id = c.base_entity_id " +
                    "where c.base_entity_id = '" + baseEntityID + "' " +
                    "and  m.date_removed is null and m.is_closed = 0 " +
                    " AND CASE WHEN c.gender = 'Male' \n" +
                    " THEN ((( julianday('now') - julianday(c.dob))/365.25) < 5)\n" +
                    " WHEN c.gender = 'Female' \n" +
                    " THEN (((( julianday('now') - julianday(c.dob))/365.25) < 5) OR (((julianday('now') - julianday(c.dob))/365.25) BETWEEN 9 AND 11)) END ";
        }
    }

    public static Child getChild(String baseEntityID) {
        String sql = getChildQuery(baseEntityID);

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());
        if (values == null || values.size() != 1)
            return null;

        return values.get(0);
    }
}
