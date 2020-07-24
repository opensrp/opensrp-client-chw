package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VisitDao extends AbstractDao {

    public static String getMUACValue(String baseEntityID) {
        String sql = String.format("select details, max(visit_date)  \n" +
                "                from visit_details d  \n" +
                "                inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE  \n" +
                "                where base_entity_id = '%s' COLLATE NOCASE \n" +
                "                and visit_key == 'muac'", baseEntityID);

        DataMap<String> dataMap = c -> getCursorValue(c, "details");

        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return "";

        return values.get(0) == null ? "" : values.get(0); // Return a default value of Low
    }
}
