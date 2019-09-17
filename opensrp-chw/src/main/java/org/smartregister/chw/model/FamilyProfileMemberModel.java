package org.smartregister.chw.model;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.chw.core.model.CoreFamilyProfileMemberModel;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileMemberModel extends CoreFamilyProfileMemberModel {

    @Override
    public String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("LEFT JOIN " + CoreConstants.TABLE_NAME.CHILD + " ON  " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        return queryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("LEFT JOIN " + CoreConstants.TABLE_NAME.CHILD + " ON  " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + " ON  " + CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        return queryBuilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = super.mainColumns(tableName);
        String[] newColumns = new String[]{
                tableName + "." + ChildDBConstants.KEY.ENTITY_TYPE,
                tableName + "." + CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + ".relationalid",
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.FIRST_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.MIDDLE_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_NAME,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.UNIQUE_ID,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.GENDER,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOD,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.ENTITY_TYPE,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.SCHEDULE_NAME,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.DUE_DATE,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.OVER_DUE_DATE,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.NOT_DONE_DATE,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.EXPIRY_DATE,
                CoreConstants.TABLE_NAME.SCHEDULE_SERVICE + "." + ChildDBConstants.KEY.COMPLETION_DATE
        };

        return ArrayUtils.addAll(columns, newColumns);
    }
}
