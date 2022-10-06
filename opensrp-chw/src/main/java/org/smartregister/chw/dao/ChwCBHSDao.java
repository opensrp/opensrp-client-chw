package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class ChwCBHSDao extends AbstractDao {
    public static boolean tbStatusAfterTestingDone(String baseEntityID) {
        String sql = "Select client_tb_status_after_testing from ec_cbhs_register where base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "client_tb_status_after_testing");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null)
            return !res.get(0).equalsIgnoreCase("unknown");
        return false;
    }

    public static Date getNextVisitDate(String baseEntityId) {
        String sql = "Select next_appointment_date from ec_cbhs_register where base_entity_id = '" + baseEntityId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "next_appointment_date");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTimeInMillis(new BigDecimal(res.get(0)).longValue());
            } catch (Exception e) {
                //NEEDED FOR THE ISSUE IN SOME TABLETS FAILING TO CREATE A TIMESTAMP
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    cal.setTime(sdf.parse(res.get(0)));
                } catch (ParseException parseException) {
                    Timber.e(parseException);
                }
            }
            return new Date(cal.getTimeInMillis());
        }
        return null;
    }

    public static boolean isDeceased(String baseEntityId) {
        String sql = " Select registration_or_followup_status\n" +
                " FROM ec_cbhs_followup ecf\n" +
                "         INNER JOIN ec_family_member efm on ecf.entity_id = efm.base_entity_id\n" +
                " WHERE efm.dod IS NULL AND ecf.entity_id = '" + baseEntityId + "'" +
                " ORDER BY ecf.last_interacted_with DESC\n" +
                " LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "registration_or_followup_status");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null) {
            return res.get(0).equals("deceased");
        }
        return false;
    }

    public static boolean completedServiceOrNoLongerContinuingWithService(String baseEntityId) {
        String sql = " Select registration_or_followup_status\n" +
                " FROM ec_cbhs_followup ecf\n" +
                "         INNER JOIN ec_family_member efm on ecf.entity_id = efm.base_entity_id\n" +
                " WHERE efm.dod IS NULL AND ecf.entity_id = '" + baseEntityId + "'" +
                " ORDER BY ecf.last_interacted_with DESC\n" +
                " LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "registration_or_followup_status");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null) {
            return res.get(0).equals("deceased") || res.get(0).equals("client_has_absconded") || res.get(0).equals("completed_and_qualified_from_the_services") || res.get(0).equals("client_relocated_to_another_location");
        }
        return false;
    }

    public static boolean hasFollowupVisits(String baseEntityId) {
        String sql = " Select ecf.entity_id\n" +
                " FROM ec_cbhs_followup ecf\n" +
                " WHERE ecf.entity_id = '" + baseEntityId + "'" +
                " ORDER BY ecf.last_interacted_with DESC\n" +
                " LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "entity_id");
        List<String> res = readData(sql, dataMap);

        return res != null && res.size() > 0;
    }
}
