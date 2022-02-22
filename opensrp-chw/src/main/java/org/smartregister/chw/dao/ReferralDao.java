package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ReferralDao extends AbstractDao {
    public static String getTaskIdByReasonReference(String formId){
        DataMap<String> dataMap = cursor -> getCursorValue(cursor,"_id");

        String sql = String.format(
                "SELECT _id FROM %s WHERE reason_reference = '%s' ",
                "task",
                formId
        );

        List<String> res = readData(sql,dataMap);
        return  res.size() > 0 ? res.get(0) : "";

    }
}
