package org.smartregister.chw.model;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.model.BaseFamilyProfileActivityModel;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileActivityModel extends BaseFamilyProfileActivityModel {
    @Override
    public String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBUilder.customJoin(String.format(" left join %s on %s.%s = %s.%s ",
                Constants.TABLE_NAME.CHILD, Constants.TABLE_NAME.CHILD_ACTIVITY, DBConstants.KEY.BASE_ENTITY_ID,
                Constants.TABLE_NAME.CHILD, DBConstants.KEY.BASE_ENTITY_ID));
        return queryBUilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
        return new String[]{
                Constants.TABLE_NAME.CHILD + ".relationalid",
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.BASE_ENTITY_ID,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.FIRST_NAME,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.MIDDLE_NAME,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.LAST_NAME,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.UNIQUE_ID,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.GENDER,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.DOB,
                Constants.TABLE_NAME.CHILD + "." + DBConstants.KEY.DOD,
                tableName + "." + DBConstants.KEY.DATE_LAST_HOME_VISIT,
                tableName + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE,
                tableName + "." + ChildDBConstants.KEY.EVENT_TYPE,
        };
    }

}
