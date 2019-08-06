package com.opensrp.chw.core.model;

import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class BaseReferralModel implements BaseReferralRegisterFragmentContract.Model {


    @Override
    public String countSelect(String taskTable, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(taskTable);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String taskTable, String mainCondition) {
        SmartRegisterQueryBuilder smartRegisterQueryBuilder = new SmartRegisterQueryBuilder();
        smartRegisterQueryBuilder.SelectInitiateMainTable(taskTable, this.mainColumns(taskTable));
        return smartRegisterQueryBuilder.mainCondition(mainCondition);
    }

    protected String[] mainColumns(String tableName) {
        String[] columns = new String[]{tableName + ".focus", tableName + ".owner", tableName + ".start"};
        return columns;
    }

}
