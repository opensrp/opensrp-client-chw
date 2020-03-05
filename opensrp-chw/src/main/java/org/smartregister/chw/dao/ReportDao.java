package org.smartregister.chw.dao;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.model.FilterReportFragmentModel;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    @NonNull
    public static List<EligibleChild> eligibleChildrenReport(String communityID, Date dueDate) {

        String _communityID = StringUtils.isBlank(communityID) ? "" : communityID;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String paramDate = sdf.format(dueDate);

        String sql = "select c.base_entity_id , c.unique_id , c.first_name , c.last_name , c.middle_name ," +
                "f.first_name family_name  , c.dob , " +
                "(select group_concat(scheduleName, ', ') from alerts where caseID = c.base_entity_id and startDate <= '" + paramDate + "') alerts " +
                "from ec_child c " +
                "left join ec_family f on c.relational_id = f.base_entity_id " +
                "inner join ec_family_member_location l on l.base_entity_id = c.base_entity_id " +
                "where  (l.location_id = '" + _communityID + "' or '" + _communityID + "' = '') " +
                "and l.base_entity_id in (select caseID from alerts where status <> 'expired' and startDate <= '" + paramDate + "') ";


        DataMap<EligibleChild> dataMap = c -> {
            EligibleChild child = new EligibleChild();
            child.setID(getCursorValue(c, "base_entity_id"));
            child.setDateOfBirth(getCursorValueAsDate(c, "dob", sdf));

            String name = getCursorValue(c, "first_name", "") + " " + getCursorValue(c, "middle_name", "");
            name = name.trim() + " " + getCursorValue(c, "middle_name", "");

            child.setFullName(name.trim());
            child.setFamilyName(getCursorValue(c, "family_name") + " Family");

            String vaccines = getCursorValue(c, "alerts", "");
            child.setDueVaccines(StringUtils.isBlank(vaccines) ? new String[]{} : vaccines.trim().split(","));

            return child;
        };

        List<EligibleChild> res = readData(sql, dataMap);

        if (res == null || res.size() == 0)
            return new ArrayList<>();

        return res;
    }

    @NonNull
    public static List<VillageDose> villageDosesReport(String villageName, String communityID, Date dueDate) {

        String _communityID = StringUtils.isBlank(communityID) ? "" : communityID;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String paramDate = sdf.format(dueDate);

        String sql = "select count(*) cnt , scheduleName " +
                "from ec_child c  " +
                "inner join ec_family_member_location l on l.base_entity_id = c.base_entity_id " +
                "inner join alerts al on caseID = c.base_entity_id " +
                "where status <> 'expired' and startDate <= '" + paramDate + "' " +
                "and  (l.location_id = '" + _communityID + "' or '" + _communityID + "' = '') " +
                "group by scheduleName " +
                "order by scheduleName";

        Map<String, Integer> map = new HashMap<>();

        DataMap<Void> dataMap = c -> {
            map.put(getCursorValue(c, "scheduleName", ""), getCursorIntValue(c, "cnt", 0));
            return null;
        };
        readData(sql, dataMap);

        VillageDose villageDose = new VillageDose();
        villageDose.setVillageName(villageName);
        villageDose.setID(communityID);
        villageDose.setRecurringServices(map);

        List<VillageDose> res = new ArrayList<>();
        res.add(villageDose);

        return res;
    }

    @NonNull
    public static List<VillageDose> villageDosesReportSummary(Date dueDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String paramDate = sdf.format(dueDate);

        String sql = "select count(*) cnt , scheduleName , location_id " +
                "from ec_child c  " +
                "inner join ec_family_member_location l on l.base_entity_id = c.base_entity_id " +
                "inner join alerts al on caseID = c.base_entity_id " +
                "where status <> 'expired' and startDate <= '" + paramDate + "' " +
                "group by scheduleName , location_id " +
                "order by location_id ";


        Map<String, LinkedHashMap<String, Integer>> resultMap = new HashMap<>();

        DataMap<Void> dataMap = c -> {
            String location_id = getCursorValue(c, "location_id", "");
            String scheduleName = getCursorValue(c, "scheduleName", "");
            Integer count = getCursorIntValue(c, "cnt", 0);

            LinkedHashMap<String, Integer> vaccineMaps = resultMap.get(location_id);
            if (vaccineMaps == null) vaccineMaps = new LinkedHashMap<>();

            vaccineMaps.put(scheduleName, count);
            resultMap.put(location_id, vaccineMaps);

            return null;
        };
        readData(sql, dataMap);

        FilterReportFragmentModel model = new FilterReportFragmentModel();
        LinkedHashMap<String, String> map = model.getAllLocations();

        List<VillageDose> result = new ArrayList<>();
        for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : resultMap.entrySet()) {
            VillageDose villageDose = new VillageDose();
            villageDose.setVillageName(map.get(entry.getKey()));
            villageDose.setID(entry.getKey());
            villageDose.setRecurringServices(entry.getValue());
            result.add(villageDose);
        }

        return result;
    }
}
