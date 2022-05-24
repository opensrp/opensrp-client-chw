package org.smartregister.chw.dao;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.domain.Child;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChwHivDao extends AbstractDao {

    public static boolean hasDueVaccines(String baseEntityID) {
        String sql = "select * FROM  count \n" +
                "FROM alerts  " +
                "where caseID = '" + baseEntityID + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.get(0) == 0)
            return false;

        res.size();
        return true;
    }
}
