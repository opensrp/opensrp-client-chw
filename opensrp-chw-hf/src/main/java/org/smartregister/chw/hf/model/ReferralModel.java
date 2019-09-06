package org.smartregister.chw.hf.model;

import org.smartregister.chw.core.model.BaseReferralModel;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReferralModel extends BaseReferralModel {

    @Override
    public String mainSelect(String tableName, String entityTable, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName, entityTable), CoreConstants.DB_CONSTANTS.ID);
        queryBuilder.customJoin(String.format("INNER JOIN %s  ON  %s.%s = %s.%s AND %s.%s = '%s' COLLATE NOCASE ", entityTable, entityTable, DBConstants.KEY.BASE_ENTITY_ID, tableName, CoreConstants.DB_CONSTANTS.FOR, tableName,CoreConstants.DB_CONSTANTS.STATUS, ChwDBConstants.TASK_STATUS_READY));
        queryBuilder.customJoin("LEFT JOIN ec_family  ON  ec_family_member.relational_id = ec_family.id COLLATE NOCASE");

        return queryBuilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName, String entityTable) {
        Set<String> columns = new HashSet<>(Arrays.asList(super.mainColumns(tableName, entityTable)));
        addClientDetails(entityTable, columns);
        addTaskDetails(columns);
        return columns.toArray(new String[]{});
    }

    private void addClientDetails(String table, Set<String> columns) {
        columns.add(table + "." + "relational_id as relationalid");
        columns.add(table + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columns.add(table + "." + DBConstants.KEY.FIRST_NAME);
        columns.add(table + "." + DBConstants.KEY.MIDDLE_NAME);
        columns.add(table + "." + DBConstants.KEY.LAST_NAME);
        columns.add(table + "." + DBConstants.KEY.DOB);
        columns.add(table + "." + DBConstants.KEY.GENDER);
        columns.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FAMILY_HEAD);

    }

    private void addTaskDetails(Set<String> columns) {
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.FOCUS);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.OWNER);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.REQUESTER);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.START);

    }
}
