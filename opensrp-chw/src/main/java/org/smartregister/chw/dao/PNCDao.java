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
                "where base_entity_id = '" + baseEntityID + "'";

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
}
