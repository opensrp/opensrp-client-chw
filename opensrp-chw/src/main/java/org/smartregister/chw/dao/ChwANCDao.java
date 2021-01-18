package org.smartregister.chw.dao;

import org.joda.time.DateTime;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChwANCDao extends AbstractDao {

    public static String getLastVisitDate(String baseEntityId) {
        try {
            String sql = "SELECT MIN(visit_date) as earliestVisitDate FROM visits WHERE base_entity_id='" + baseEntityId + "';";

            AbstractDao.DataMap<String> dataMap = cursor -> getCursorValue(cursor, "earliestVisitDate");

            return readSingleValue(sql, dataMap);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getLastContactDate(String baseEntityId) {
        try {
            String sql = "SELECT last_contact_visit lastContactVisit FROM ec_anc_register WHERE base_entity_id='" + baseEntityId + "';";

            AbstractDao.DataMap<String> dataMap = cursor -> getCursorValue(cursor, "lastContactVisit");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date lastContactDate = simpleDateFormat.parse(readSingleValue(sql, dataMap));
            Date lastVisitDate = new DateTime().withMillis(Long.parseLong(getLastVisitDate(baseEntityId))).toDate();
            if(lastVisitDate.before(lastContactDate))
                return simpleDateFormat.format(lastVisitDate);

            return simpleDateFormat.format(lastContactDate);
        } catch (Exception e) {
            return "";
        }
    }
}
