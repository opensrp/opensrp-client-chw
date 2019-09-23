package org.smartregister.chw.dao;

import org.smartregister.chw.core.dao.AbstractDao;

import java.util.Date;
import java.util.List;

public class WashCheckDao extends AbstractDao {

    public static long getLastWashCheckDate(String familyBaseEntityID) {
        String sql = "select eventDate from event where eventType = 'WASH check' and " +
                "baseEntityId = '" + familyBaseEntityID + "' order by eventDate desc limit 1";

        DataMap<Date> dataMap = c -> getCursorValueAsDate(c, "eventDate", getDobDateFormat());
        List<Date> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;

        return res.get(0).getTime();
    }
}
