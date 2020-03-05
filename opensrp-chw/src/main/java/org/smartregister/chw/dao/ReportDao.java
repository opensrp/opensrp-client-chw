package org.smartregister.chw.dao;

import androidx.annotation.NonNull;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ReportDao extends AbstractDao {

    @NonNull
    public static List<String> extractRecordedLocations() {
        String sql = "select distinct location_id from ec_family_member_location";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "location_id");
        List<String> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return new ArrayList<>();

        return res;
    }
}
