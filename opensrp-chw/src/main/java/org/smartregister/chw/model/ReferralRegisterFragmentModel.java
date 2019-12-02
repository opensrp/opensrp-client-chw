package org.smartregister.chw.model;


import androidx.annotation.NonNull;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.referral.model.BaseReferralRegisterFragmentModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.HashSet;
import java.util.Set;

public class ReferralRegisterFragmentModel extends BaseReferralRegisterFragmentModel {

    @NonNull
    @Override
    public String mainSelect(@NonNull String tableName, @NonNull String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("INNER JOIN " + Constants.TABLE_NAME.FAMILY_MEMBER + " ON  " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + Constants.TABLE_NAME.FAMILY + " ON  " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID + " = " + Constants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.BASE_ENTITY_ID);
        queryBuilder.customJoin("INNER JOIN " + org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL_SERVICE + " ON  " + tableName + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_SERVICE_ID + " = " + org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL_SERVICE + "." + org.smartregister.chw.referral.util.DBConstants.KEY.ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN location ON  " + tableName + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_HF + " = location.uuid COLLATE NOCASE ");
        return queryBuilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
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
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.VILLAGE_TOWN);
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FAMILY_HEAD);
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.PRIMARY_CAREGIVER);
        columnList.add("location.name  AS " + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_HF);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL_SERVICE + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_SERVICE_IDENTIFIER + "  AS " + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_SERVICE);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_DATE);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_STATUS);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + org.smartregister.chw.referral.util.DBConstants.KEY.PROBLEMS_IDS);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_REASON);
        columnList.add(org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + org.smartregister.chw.referral.util.DBConstants.KEY.REFERRAL_APPOINTMENT_DATE);
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FIRST_NAME + " as " + org.smartregister.chw.anc.util.DBConstants.KEY.FAMILY_NAME);
        return columnList.toArray(new String[columnList.size()]);
    }
}
