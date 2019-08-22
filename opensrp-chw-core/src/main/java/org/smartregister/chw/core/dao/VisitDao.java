package org.smartregister.chw.core.dao;

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
                " where base_entity_id = '" + baseEntityID + "' " +
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
        String sql = "select date_created from ec_child where base_entity_id = '" + baseEntityID + "'";

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

        Long date = calendar.getTime().getTime();

        String sql = "delete from visits where base_entity_id = '" + baseEntityID + "' and visit_type < '" +
                CoreConstants.EventType.CHILD_VISIT_NOT_DONE + "' and visit_date >= " + date + " and created_at >=  " + date + "";
        updateDB(sql);
    }
}
