package org.smartregister.chw.model;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.model.BaseFamilyProfileDueModel;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileDueModel extends BaseFamilyProfileDueModel {

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("LEFT JOIN " + CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + " ON  " + CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        return queryBuilder.mainCondition(mainCondition);
    }

    protected String[] mainColumns(String tableName) {
        return new String[]{
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + ".relationalid",
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.FIRST_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.MIDDLE_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.UNIQUE_ID,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.GENDER,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOD//,
                //tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT,
                //tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE,
                //tableName + "." + ChildDBConstants.KEY.DATE_CREATED
        };
    }
}
