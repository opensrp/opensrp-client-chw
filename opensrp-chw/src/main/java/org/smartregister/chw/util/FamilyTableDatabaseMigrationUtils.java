package org.smartregister.chw.util;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;

import timber.log.Timber;

public class FamilyTableDatabaseMigrationUtils extends AbstractDao {

    public static void fillFamilyTableWithProviderIds(SQLiteDatabase db) {
        ArrayList<String> baseEntityIds = getBaseEntityIdsFromFamilyTable(db);
        ArrayList<String> jsonLists = getJSONListAgainstBaseEntityIds(db, baseEntityIds);
        if (db != null && db.isOpen())
            db.rawExecSQL(buildUpdateQueryForFamilyTableWithBaseEntityIds(jsonLists));
    }

    private static String getDemo() {
        return "UPDATE ec_family SET provider_id = (case when base_entity_id = '047e55ec-3710-49bf-8fbd-aa620c687dd8' then 'demo' when base_entity_id = 'fc0d4818-5040-47d4-8077-d7f4c3e021b8' then 'demo1' end)";
    }

    public static String buildUpdateQueryForFamilyTableWithBaseEntityIds(ArrayList<String> jsonLists) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ec_family SET provider_id = (case ");
        for (String item : jsonLists) {
            try {
                JSONObject jsonObject = new JSONObject(item);
                String baseEntityId = jsonObject.getString("baseEntityId");
                String providerId = jsonObject.getString("providerId");

                queryBuilder.append("when base_entity_id = '")
                        .append(baseEntityId)
                        .append("' then '").append(providerId).append("' ");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        queryBuilder.append(" end)");
        return queryBuilder.toString();
    }

    private static ArrayList<String> getJSONListAgainstBaseEntityIds(SQLiteDatabase db, ArrayList<String> baseEntityIds) {
        String _BaseEntityIDs = "('" + StringUtils.join(baseEntityIds, "','") + "')";
        ArrayList<String> JSONLists = new ArrayList<>();
        try {
            String query = "SELECT json FROM event WHERE baseEntityId IN " + _BaseEntityIDs;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONLists.add(cursor.getString(cursor.getColumnIndex("json")));
                }
            }
            return JSONLists;
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return JSONLists;
    }

    private static ArrayList<String> getBaseEntityIdsFromFamilyTable(SQLiteDatabase db) {
        ArrayList<String> baseEntityIds = new ArrayList<>();
        try {
            String query = "SELECT base_entity_id FROM ec_family";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    baseEntityIds.add(cursor.getString(cursor.getColumnIndex("base_entity_id")));
                }
            }
            return baseEntityIds;
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return baseEntityIds;
    }

}
