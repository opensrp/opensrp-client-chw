package org.smartregister.brac.hnpp.model;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.model.BaseFamilyProfileActivityModel;
import org.smartregister.family.util.DBConstants;

public class MemberProfileActivityModel extends BaseFamilyProfileActivityModel {
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
                CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.CHILD_ACTIVITY, DBConstants.KEY.BASE_ENTITY_ID,
                CoreConstants.TABLE_NAME.CHILD, DBConstants.KEY.BASE_ENTITY_ID));
        return queryBUilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
        return new String[]{
                CoreConstants.TABLE_NAME.CHILD + ".relationalid",
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.BASE_ENTITY_ID,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.FIRST_NAME,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.MIDDLE_NAME,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.LAST_NAME,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.UNIQUE_ID,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.GENDER,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.DOB,
                CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.DOD,
                tableName + "." + DBConstants.KEY.DATE_LAST_HOME_VISIT,
                tableName + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE,
                tableName + "." + ChildDBConstants.KEY.EVENT_TYPE,
        };
    }

}
