package org.smartregister.chw.dao;

import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.dao.AbstractDao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyKitDao extends AbstractDao {

    public static long getLastFamilyKitDate(String familyBaseEntityID) {
        String sql = "select eventDate from event where eventType = 'Family Kit' and " +
                "baseEntityId = '" + familyBaseEntityID + "' order by eventDate desc limit 1";

        DataMap<Date> dataMap = c -> getCursorValueAsDate(c, "eventDate", getDobDateFormat());
        List<Date> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;

        return res.get(0).getTime();
    }

    public static Map<String, VisitDetail> getFamilyKitDetails(Long familyKitDate, String baseEntityID) {
        String sql = "select v.visit_date,  vd.visit_key , vd.parent_code , vd.preprocessed_type , vd.details, vd.human_readable_details , vd.visit_id , v.base_entity_id from visits v " +
                "inner join visit_details vd on vd.visit_id = v.visit_id and v.base_entity_id = '" + baseEntityID + "' " +
                "where v.visit_date = " + familyKitDate + " and v.visit_type = 'Family Kit'";

        Map<String, VisitDetail> map = new HashMap<>();

        DataMap<VisitDetail> dataMap = c -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitId(getCursorValue(c, "visit_id"));
            detail.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            detail.setVisitKey(getCursorValue(c, "visit_key"));
            detail.setParentCode(getCursorValue(c, "parent_code"));
            detail.setPreProcessedType(getCursorValue(c, "preprocessed_type"));
            detail.setDetails(getCursorValue(c, "details"));
            detail.setHumanReadable(getCursorValue(c, "human_readable_details"));

            map.put(detail.getVisitKey(), detail);

            return detail;
        };

        List<VisitDetail> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return new HashMap<>();

        return map;
    }


}
