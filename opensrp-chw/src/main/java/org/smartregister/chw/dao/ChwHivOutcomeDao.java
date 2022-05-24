package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChwHivOutcomeDao extends AbstractDao {

    public static String servicesProvided(String baseEntityID, long timestamp) {
        String sql = "SELECT action_taken, test_results\n" +
                "FROM ec_hiv_outcome\n" +
                "WHERE entity_id = '" + baseEntityID + "'\n" +
                "AND date(visit_date) = date(substr(strftime('%Y-%m-%d', datetime(" + timestamp + " / 1000, 'unixepoch', 'localtime')), 1, 4) ||\n" +
                "                                 '-' ||\n" +
                "                                 substr(strftime('%Y-%m-%d', datetime(" + timestamp + "/ 1000, 'unixepoch', 'localtime')), 6, 2) ||\n" +
                "                                 '-' ||  substr(strftime('%Y-%m-%d', datetime(" + timestamp + "/ 1000, 'unixepoch', 'localtime')), 9, 2))\n" +
                "ORDER BY visit_date DESC\n" +
                "LIMIT 1;";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "action_taken");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0 && res.get(0) != null)
            return res.get(0);
        return null;
    }

    public static String hivStatus(String baseEntityID, long timestamp) {
        String sql = "SELECT test_results\n" +
                "FROM ec_hiv_outcome\n" +
                "WHERE entity_id = '" + baseEntityID + "'\n" +
                "AND date(visit_date) = date(substr(strftime('%Y-%m-%d', datetime(" + timestamp + " / 1000, 'unixepoch', 'localtime')), 1, 4) ||\n" +
                "                                 '-' ||\n" +
                "                                 substr(strftime('%Y-%m-%d', datetime(" + timestamp + "/ 1000, 'unixepoch', 'localtime')), 6, 2) ||\n" +
                "                                 '-' ||  substr(strftime('%Y-%m-%d', datetime(" + timestamp + "/ 1000, 'unixepoch', 'localtime')), 9, 2))\n" +
                "ORDER BY visit_date DESC\n" +
                "LIMIT 1;";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "test_results");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0 && res.get(0) != null)
            return res.get(0);
        return null;
    }
}
