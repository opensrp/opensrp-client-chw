package org.smartregister.chw.dao;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.dao.AbstractDao;

import java.util.Date;
import java.util.List;

public class RoutineHouseHoldDao extends AbstractDao {

    public static long getLastRoutineVisitkDate(String familyBaseEntityID) {
        String sql = "select eventDate from event where eventType = '" + CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT + "' and " +
                "baseEntityId = '" + familyBaseEntityID + "' order by eventDate desc limit 1";

        AbstractDao.DataMap<Date> dataMap = c -> getCursorValueAsDate(c, "eventDate", getDobDateFormat());
        List<Date> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;

        return res.get(0).getTime();
    }
}
