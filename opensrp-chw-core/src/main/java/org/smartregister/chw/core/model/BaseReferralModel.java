package org.smartregister.chw.core.model;

import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class BaseReferralModel implements BaseReferralRegisterFragmentContract.Model {


    @Override
    public String countSelect(String taskTable, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(taskTable);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String taskTable, String entityTable, String mainCondition) {
        SmartRegisterQueryBuilder smartRegisterQueryBuilder = new SmartRegisterQueryBuilder();
        smartRegisterQueryBuilder.selectInitiateMainTable(taskTable, this.mainColumns(taskTable, entityTable), DB_CONSTANTS.ID);
        return smartRegisterQueryBuilder.mainCondition(mainCondition);
    }

    protected String[] mainColumns(String tableName, String entityTable) {
        return new String[]{
                tableName + "." + DB_CONSTANTS.FOCUS,
                tableName + "." + DB_CONSTANTS.REQUESTER,
                tableName + "." + DB_CONSTANTS.START};
    }

}
