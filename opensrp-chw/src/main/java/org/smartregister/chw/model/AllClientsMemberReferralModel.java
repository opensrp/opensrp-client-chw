package org.smartregister.chw.model;


import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.referral.model.BaseIssueReferralModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.HashSet;
import java.util.Set;

public class AllClientsMemberReferralModel extends BaseIssueReferralModel {

    @NonNull
    @Override
    public String mainSelect(@NonNull String tableName, @NonNull String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        return queryBuilder.mainCondition(mainCondition);
    }

    @NotNull
    @Override
    public String[] mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();

        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.GENDER);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID);
        return columnList.toArray(new String[0]);
    }
}
