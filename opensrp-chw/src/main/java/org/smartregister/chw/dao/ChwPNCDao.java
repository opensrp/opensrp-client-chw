package org.smartregister.chw.dao;

import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.dao.AbstractDao;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChwPNCDao extends AbstractDao {

    public static @Nullable PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID) {
        return getLastHealthFacilityVisitSummary(baseEntityID, null);
    }

    public static @Nullable PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID, SQLiteDatabase sqLiteDatabase) {
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
        List<PNCHealthFacilityVisitSummary> res;
        if (sqLiteDatabase != null) {
            res = AbstractDao.readData(sql, dataMap, sqLiteDatabase);
        } else {
            res = AbstractDao.readData(sql, dataMap);
        }
        return (res != null && res.size() > 0) ? res.get(0) : null;
    }


    public static Map<String,String> getPNCImmunizationAtBirth(String motherBaseEntityId){
        String sql = "SELECT vd.visit_key, vd.details FROM Visit_details vd  INNER JOIN visits v on v.visit_id = vd.visit_id AND v.visit_type = 'Immunization Visit' AND v.base_entity_id =" +
                " (SELECT ch.base_entity_id FROM ec_child ch INNER JOIN ec_pregnancy_outcome pg ON pg.base_entity_id = ch.mother_entity_id WHERE pg.base_entity_id = '" + motherBaseEntityId + "' COLLATE NOCASE )";


        DataMap<Pair<String, String>> dataMap = c -> Pair.create(getCursorValue(c, "visit_key"), getCursorValue(c, "details"));

        Map<String, String> immunizations = new HashMap<>();
        List<Pair<String, String>> pairs = AbstractDao.readData(sql, dataMap);
        if (pairs == null || pairs.size() == 0)
            return immunizations;

        for (Pair<String, String> pair : pairs) {
            immunizations.put(pair.first, pair.second);
        }

        return immunizations;
    }

    public interface Flavor {

    }
}
