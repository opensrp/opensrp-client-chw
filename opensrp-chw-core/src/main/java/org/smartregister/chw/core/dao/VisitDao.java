package org.smartregister.chw.core.dao;

import android.util.Pair;

import org.smartregister.chw.core.domain.VisitSummary;
import org.smartregister.chw.core.utils.CoreConstants;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.Nullable;
import timber.log.Timber;

public class VisitDao extends AbstractDao {

    @Nullable
    public static Map<String, VisitSummary> getVisitSummary(String baseEntityID) {
        String sql = "select base_entity_id , visit_type , max(visit_date) visit_date from visits " +
                " where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE " +
                " group by base_entity_id , visit_type ";

        DataMap<VisitSummary> dataMap = c -> {
            Long visit_date = getCursorLongValue(c, "visit_date");
            return new VisitSummary(
                    getCursorValue(c, "visit_type"),
                    visit_date != null ? new Date(visit_date) : null,
                    getCursorValue(c, "base_entity_id")
            );
        };

        List<VisitSummary> summaries = AbstractDao.readData(sql, dataMap);
        if (summaries == null)
            return null;

        Map<String, VisitSummary> map = new HashMap<>();
        for (VisitSummary summary : summaries) {
            map.put(summary.getVisitType(), summary);
        }

        return map;
    }

    public static Long getChildDateCreated(String baseEntityID) {
        String sql = "select date_created from ec_child where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE ";

        DataMap<String> dataMap = c -> getCursorValue(c, "date_created");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return null;

        try {
            return getDobDateFormat().parse(values.get(0)).getTime();
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    public static void undoChildVisitNotDone(String baseEntityID) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        long date = calendar.getTime().getTime();

        String sql = "delete from visits where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and visit_type = '" +
                CoreConstants.EventType.CHILD_VISIT_NOT_DONE + "' and visit_date >= " + date + " and created_at >=  " + date + "";
        updateDB(sql);
    }

    public static boolean memberHasBirthCert(String baseEntityID) {
        String sql = "select count(*) certificates " +
                "from visit_details d " +
                "inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE " +
                "where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and v.processed = 1 " +
                "and (visit_key in ('birth_certificate','birth_cert') and details = 'GIVEN' or human_readable_details = 'Yes')";

        DataMap<String> dataMap = c -> getCursorValue(c, "certificates");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static boolean memberHasVaccineCard(String baseEntityID) {
        String sql = "select count(*) certificates " +
                "from visit_details d " +
                "inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE " +
                "where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and v.processed = 1 " +
                "and (visit_key in ('vaccine_card') and human_readable_details = 'Yes')";

        DataMap<String> dataMap = c -> getCursorValue(c, "certificates");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static boolean memberHasVisits(String baseEntityID) {
        String sql = "select count(*) total from visits where base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "total");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static Map<String, Date> getUprocessedVaccines(String baseEntityID) {
        String sql = "select vd.visit_key , vd.details " +
                "from visit_details vd " +
                "inner join visits v on v.visit_id = vd.visit_id " +
                "where v.base_entity_id = '" + baseEntityID + "'" +
                "and v.processed = 0 and vd.parent_code = 'vaccine' and vd.details <> 'Vaccine not given'";

        Map<String, Date> res = new HashMap<>();

        DataMap<Pair<String, String>> dataMap = c -> Pair.create(getCursorValue(c, "visit_key"), getCursorValue(c, "details"));
        List<Pair<String, String>> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return res;

        for (Pair<String, String> pair : values) {
            try {
                res.put(pair.first, getDobDateFormat().parse(pair.second));
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return res;
    }
}
