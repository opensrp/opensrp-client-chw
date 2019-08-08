package com.opensrp.chw.hf.model;

import com.opensrp.chw.core.model.BaseReferralModel;
import com.opensrp.chw.core.utils.CoreConstants.DB_CONSTANTS;
import com.opensrp.chw.core.utils.CoreConstants.TABLE_NAME;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReferralModel extends BaseReferralModel {

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName), DB_CONSTANTS.ID);
        queryBuilder.customJoin(String.format("INNER JOIN %s  ON  %s.%s = %s.%s COLLATE NOCASE ",
                TABLE_NAME.CHILD, TABLE_NAME.CHILD, DBConstants.KEY.BASE_ENTITY_ID, tableName, DB_CONSTANTS.FOR));

        return queryBuilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
        Set<String> columns = new HashSet<>(Arrays.asList(super.mainColumns(tableName)));
        addClientDetails(TABLE_NAME.CHILD, columns);
        addTaskDetails(TABLE_NAME.TASK, columns);
        return columns.toArray(new String[]{});
    }

    private void addClientDetails(String table, Set<String> columns) {
        columns.add(table + "." + "relationalid");
        columns.add(table + "." + DBConstants.KEY.FIRST_NAME);
        columns.add(table + "." + DBConstants.KEY.MIDDLE_NAME);
        columns.add(table + "." + DBConstants.KEY.LAST_NAME);
        columns.add(table + "." + DBConstants.KEY.DOB);
        columns.add(table + "." + DBConstants.KEY.GENDER);

    }

    private void addTaskDetails(String table, Set<String> columns) {
        columns.add(table + "." + DB_CONSTANTS.FOCUS);
        columns.add(table + "." + DB_CONSTANTS.REQUESTER);
        columns.add(table + "." + DB_CONSTANTS.START);

    }
}
