package org.smartregister.chw.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.core.dao.AbstractDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;

import java.text.ParseException;
import java.util.List;

import timber.log.Timber;

public class PNCDao extends AbstractDao {

    public static @Nullable PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID) {
        String sql = "select  last_health_facility_visit_date , confirmed_health_facility_visits, delivery_date from ec_pregnancy_outcome " +
                "where base_entity_id = '" + baseEntityID + "'" + " COLLATE NOCASE ";

        DataMap<PNCHealthFacilityVisitSummary> dataMap = c -> {
            try {
                return new PNCHealthFacilityVisitSummary(
                        getCursorValue(c, "delivery_date"),
                        getCursorValue(c, "last_health_facility_visit_date"),
                        getCursorValue(c, "confirmed_health_facility_visits")
                );
            } catch (ParseException e) {
                Timber.e(e);
            }
            return null;
        };

        List<PNCHealthFacilityVisitSummary> res = AbstractDao.readData(sql, dataMap);
        return (res != null && res.size() > 0) ? res.get(0) : null;
    }

    public static boolean isPncMember(String baseEntityID) {
        String sql = "select count(*) count from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "' and is_closed = 0";
        DataMap<Integer> dataMap = (cursor) -> {
            return getCursorIntValue(cursor, "count");
        };
        List<Integer> res = readData(sql, dataMap);
        if (res != null && res.size() == 1) {
            return (Integer)res.get(0) > 0;
        } else {
            return false;
        }
    }

    public static boolean hasFamilyPlanning(String baseEntityID) {
        String sql = "select count(*) records from visit_details vd  " +
                "inner join visits v on vd.visit_id = v.visit_id COLLATE NOCASE and vd.visit_key = 'fp_method' and vd.human_readable_details <> 'None' " +
                "where v.base_entity_id =  '" + baseEntityID + "' and v.processed = 1 ";

        DataMap<Integer> dataMap = c -> getCursorIntValue(c, "records");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() < 1)
            return false;

        return res.get(0) > 0;
    }
}
