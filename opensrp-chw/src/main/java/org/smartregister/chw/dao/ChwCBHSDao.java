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

    public static Date getNextVisitDate(String baseEntityId){
        String sql = "Select next_appointment_date from ec_cbhs_register where base_entity_id = '" + baseEntityId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "next_appointment_date");
        List<String> res = readData(sql, dataMap);

        if (res != null && res.size() > 0 && res.get(0) != null){
            Calendar cal = Calendar.getInstance();
            try{
                cal.setTimeInMillis(new BigDecimal(res.get(0)).longValue());
            }catch (Exception e){
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
}
