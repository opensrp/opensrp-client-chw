package org.smartregister.chw.processor;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.chw.util.Constants;
import org.smartregister.reporting.domain.CompositeIndicatorTally;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.MultiValueIndicatorTally;
import org.smartregister.reporting.exception.MultiResultProcessorException;
import org.smartregister.reporting.processor.MultiResultProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This processor is able to processor queries where the first, second and third column returned are indicator groupings
 * eg. vaccine as the first column, gender as the second column and provider as the third column while the fourth column contains the count.
 * The three indicator groups should be a string or have default affinity to {@link android.database.Cursor}.FIELD_TYPE_STRING
 * as described https://www.sqlite.org/datatype3.html.
 * <p>
 * The fourth column should have affinity to either Cursor.FIELD_TYPE_INTEGER or Cursor.FIELD_TYPE_FLOAT in SQLite
 * <p>
 * Created by Allan Onchuru - aonchuru@ona.io on 31-March-2022
 */

public class ChwMultiResultsProcessor implements MultiResultProcessor {

    @Override
    public boolean canProcess(int columns, @NonNull String[] columnNames) {
        return (columns == 4 && columnNames.length == 4 && columnNames[3].contains("count")) ||
                (columns == 3 && columnNames.length == 3 && columnNames[2].contains("count"));
    }

    @NonNull
    @Override
    public List<IndicatorTally> processMultiResultTally(@NonNull CompositeIndicatorTally compositeIndicatorTally) throws MultiResultProcessorException {
        ArrayList<Object[]> compositeTallies = new Gson().fromJson(compositeIndicatorTally.getValueSet(), new TypeToken<List<Object[]>>() {
        }.getType());

        // Column names
        Object[] compositeTallyColumns = compositeTallies.get(0);
        // Remove the column names from processing
        compositeTallies.remove(0);

        List<IndicatorTally> tallies = new ArrayList<>();

        for (Object[] compositeTally : compositeTallies) {
            HashMap<String, String> multiValuesMap = new HashMap<>();
            MultiValueIndicatorTally indicatorTally = new MultiValueIndicatorTally();
            indicatorTally.setCreatedAt(compositeIndicatorTally.getCreatedAt());
            indicatorTally.setGrouping(compositeIndicatorTally.getGrouping());

            if (compositeTally.length == 4) {
                indicatorTally.setIndicatorCode(compositeIndicatorTally.getIndicatorCode() +
                        Constants.MultiResultProcessor.GROUPING_SEPARATOR + compositeTally[0]
                        + Constants.MultiResultProcessor.GROUPING_SEPARATOR + compositeTally[1]);
                // Add other values of interest
                multiValuesMap.put(compositeTallyColumns[1].toString(), compositeTally[1].toString());
                multiValuesMap.put(compositeTallyColumns[2].toString(), compositeTally[2].toString());
                indicatorTally.setMultiValuesMap(multiValuesMap);

                Object indicatorValue = compositeTally[3];
                if (indicatorValue instanceof Integer) {
                    indicatorTally.setCount((int) indicatorValue);
                } else if (indicatorValue instanceof Double) {
                    indicatorTally.setCount(((Double) indicatorValue).floatValue());
                } else {
                    throw new MultiResultProcessorException(indicatorValue, compositeIndicatorTally);
                }

                tallies.add(indicatorTally);
            } else if (compositeTally.length == 3) {
                indicatorTally.setIndicatorCode(compositeIndicatorTally.getIndicatorCode() +
                        Constants.MultiResultProcessor.GROUPING_SEPARATOR + compositeTally[0]);
                // Add other values of interest
                multiValuesMap.put(compositeTallyColumns[1].toString(), compositeTally[1].toString());
                indicatorTally.setMultiValuesMap(multiValuesMap);

                Object indicatorValue = compositeTally[2];
                if (indicatorValue instanceof Integer) {
                    indicatorTally.setCount((int) indicatorValue);
                } else if (indicatorValue instanceof Double) {
                    indicatorTally.setCount(((Double) indicatorValue).floatValue());
                } else {
                    throw new MultiResultProcessorException(indicatorValue, compositeIndicatorTally);
                }
                tallies.add(indicatorTally);
            }
        }
        return tallies;
    }
}
