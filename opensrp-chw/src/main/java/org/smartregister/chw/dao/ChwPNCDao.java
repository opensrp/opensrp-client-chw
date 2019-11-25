package org.smartregister.chw.dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.dao.AbstractDao;

import java.text.ParseException;
import java.util.List;

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


    public interface Flavor {

    }
}
