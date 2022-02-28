package org.smartregister.chw.util;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NAME;
import static org.smartregister.chw.util.Constants.DATE_DIED;
import static org.smartregister.chw.util.Constants.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.util.Constants.DEATH_PLACE;
import static org.smartregister.chw.util.Constants.MARITAL_STATUS;
import static org.smartregister.chw.util.Constants.NATIONALITY;
import static org.smartregister.chw.util.Constants.NATIONAL_ID;
import static org.smartregister.chw.util.Constants.REMOVE_REASON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;

public abstract class OutOfAreaDeathUtils {

    public static Gson gsonConverter;

    static {
        gsonConverter = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>) (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .registerTypeAdapter(DateTime.class, (JsonDeserializer<DateTime>) (json, typeOfT, context) -> new DateTime(json.getAsJsonPrimitive().getAsString()))
                .create();
    }

    public static String mainSelectRegisterWithoutGroupby(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName));
        return queryBuilder.mainCondition(mainCondition);
    }

    public static String[] mainColumns(String tableName) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(tableName + "." + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(tableName + "." + NAME);
        columnList.add(tableName + "." + NATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.DOB);
        columnList.add(tableName + "." + REMOVE_REASON);
        columnList.add(tableName + "." + DATE_DIED);
        columnList.add(tableName + "." + DEATH_PLACE);
        columnList.add(tableName + "." + NATIONALITY);
        columnList.add(tableName + "." + MARITAL_STATUS);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DEATH_CERTIFICATE_NUMBER);
        return columnList.toArray(new String[columnList.size()]);
    }

    public interface Flavor {
        String[] getOneYearVaccines();
        String[] getTwoYearVaccines();
    }
}
