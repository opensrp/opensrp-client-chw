package org.smartregister.chw.util;

import android.app.Activity;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.chw.R;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.MultiValueIndicatorTally;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervisorDashboardUtil {

    public static List<String> getHouseHoldsWithIncompleteTasksData(List<Map<String, IndicatorTally>> indicatorTallies, Activity context) {
        List<String> tableDataList = new ArrayList<>();
        int totalHouseholds;
        int houseHoldsWithOpenTasks;
        int ratio;
        for (Map<String, IndicatorTally> indicatorTallyMap : indicatorTallies) {
            for (String key : indicatorTallyMap.keySet()) {

                if (key.contains(ReportingConstants.SupervisorIndicatorKeys.SUPERVISOR_HOUSEHOLDS_WITH_OPEN_TASKS)) {
                    totalHouseholds = (int)Double.parseDouble(((MultiValueIndicatorTally)indicatorTallyMap.get(key)).getMultiValuesMap().get("total_households"));
                    houseHoldsWithOpenTasks = indicatorTallyMap.get(key).getCount();
                    ratio = (totalHouseholds > 0 && houseHoldsWithOpenTasks > 0) ? (houseHoldsWithOpenTasks / totalHouseholds) * 100 : 0;
                    tableDataList.add(getProvider(key));
                    tableDataList.add(ratio + "%");
                    tableDataList.add(String.format("%s %s", houseHoldsWithOpenTasks, context.getString(R.string.households)));
                }
            }
        }
        return tableDataList;
    }

    public static List<String> getIncompleteTasksData(List<Map<String, IndicatorTally>> indicatorTallies, Activity context) {
        int closedTasksCount = 0;
        int openTasksCount = 0;
        String provider = null;
        Map<String, Pair<Integer, Integer>> chaMap = new HashMap<>();
        for (Map<String, IndicatorTally> indicatorTallyMap : indicatorTallies) {
            for (String key : indicatorTallyMap.keySet()) {
                if (key.contains(ReportingConstants.SupervisorIndicatorKeys.SUPERVISOR_TASK_COMPLETION_OPEN_TASKS)) {
                    provider = getProvider(key);
                    openTasksCount = indicatorTallyMap.get(key).getCount();
                    if (chaMap.containsKey(provider)) {
                        closedTasksCount = chaMap.get(provider).getLeft() != null ? chaMap.get(provider).getLeft() : 0;
                    }

                } else if (key.contains(ReportingConstants.SupervisorIndicatorKeys.SUPERVISOR_TASK_COMPLETION_CLOSED_TASKS)) {
                    provider = getProvider(key);
                    closedTasksCount = indicatorTallyMap.get(key).getCount();
                    if (chaMap.containsKey(provider)) {
                        openTasksCount = chaMap.get(provider).getRight() != null ? chaMap.get(provider).getRight() : 0;
                    }
                }
                if (provider != null)
                    chaMap.put(provider, Pair.of(closedTasksCount, openTasksCount));
            }
        }
        int totalTasks;
        int ratio;
        List<String> tableDataList = new ArrayList<>();
        for (Map.Entry<String, Pair<Integer, Integer>> entry : chaMap.entrySet()) {
            closedTasksCount = entry.getValue().getLeft();
            openTasksCount = entry.getValue().getRight();
            totalTasks = closedTasksCount + openTasksCount;
            ratio = (closedTasksCount > 0 && openTasksCount > 0) ? (openTasksCount / totalTasks) * 100 : 0;
            tableDataList.add(entry.getKey().toUpperCase());
            tableDataList.add(ratio + "%");
            tableDataList.add(String.format("%s %s",openTasksCount, context.getString(R.string.tasks)));

        }
        return tableDataList;
    }

    public static List<String> getLastSyncByChwData(List<Map<String, IndicatorTally>> indicatorTallies) {
        List<String> tableDataList = new ArrayList<>();
        for (Map<String, IndicatorTally> indicatorTallyMap : indicatorTallies) {
            for (String key : indicatorTallyMap.keySet()) {
                if (key.contains(ReportingConstants.SupervisorIndicatorKeys.SUPERVISOR_LAST_SYNC_BY_CHW)) {
                    tableDataList.add(getProvider(key).toUpperCase());
                    tableDataList.add(""); // Empty center column (spacer)
                    tableDataList.add(((MultiValueIndicatorTally)indicatorTallyMap.get(key)).getMultiValuesMap().get("last_sync_date"));
                }
            }
        }
        return tableDataList;
    }

    private static String getProvider(String indicatorKey) {
        String[] indicatorKeywords = indicatorKey.split(Constants.MultiResultProcessor.GROUPING_SEPARATOR);
        return indicatorKeywords[indicatorKeywords.length - 1];
    }
}
