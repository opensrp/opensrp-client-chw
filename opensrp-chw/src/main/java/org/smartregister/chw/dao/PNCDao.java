package org.smartregister.chw.dao;

import android.database.Cursor;

import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;

import java.text.ParseException;
import java.util.List;

public class PNCDao extends AbstractDao {

    public static PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID) {
        String sql = "select  last_health_facility_visit_date , confirmed_health_facility_visits, delivery_date from ec_pregnancy_outcome " +
                "where base_entity_id = '" + baseEntityID + "'";

        DataMap<PNCHealthFacilityVisitSummary> dataMap = new DataMap<PNCHealthFacilityVisitSummary>() {
            @Override
            public PNCHealthFacilityVisitSummary readCursor(Cursor c) {
                try {
                    return new PNCHealthFacilityVisitSummary(
                            getCursorValue(c, "delivery_date"),
                            getCursorValue(c, "last_health_facility_visit_date"),
                            getCursorValue(c, "confirmed_health_facility_visits")
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        List<PNCHealthFacilityVisitSummary> res = AbstractDao.readData(sql, dataMap);
        return (res != null && res.size() > 0) ? res.get(0) : null;
    }
}
