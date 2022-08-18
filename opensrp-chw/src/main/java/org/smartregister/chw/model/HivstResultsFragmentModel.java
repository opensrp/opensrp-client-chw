package org.smartregister.chw.model;

import androidx.annotation.NonNull;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.hivst.model.BaseHivstResultsFragmentModel;

import org.smartregister.chw.hivst.util.DBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import java.util.HashSet;
import java.util.Set;

public class HivstResultsFragmentModel extends BaseHivstResultsFragmentModel {
    @NonNull
    @Override
    public String mainSelect(@NonNull String tableName, @NonNull String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("INNER JOIN " + Constants.TABLE_NAME.FAMILY_MEMBER + " ON  " + tableName + "." + org.smartregister.chw.pmtct.util.DBConstants.KEY.ENTITY_ID + "= " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + Constants.TABLE_NAME.FAMILY + " ON  " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.RELATIONAL_ID + " = " + Constants.TABLE_NAME.FAMILY + "." + org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID);
        queryBuilder.customJoin("LEFT JOIN " + Constants.TABLE_NAME.FAMILY_MEMBER + " as T1 ON  " + Constants.TABLE_NAME.FAMILY + "." + org.smartregister.family.util.DBConstants.KEY.PRIMARY_CAREGIVER + " = T1." + org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID);
        queryBuilder.customJoin("LEFT JOIN " + Constants.TABLE_NAME.FAMILY_MEMBER + " as T2 ON  " + Constants.TABLE_NAME.FAMILY + "." + org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD + " = T2." + org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID);
        return queryBuilder.mainCondition(mainCondition);
    }

    protected String[] mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();
        columnList.add(tableName + "." + DBConstants.KEY.ENTITY_ID + " as " + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " as " + DBConstants.KEY.ENTITY_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.FIRST_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.MIDDLE_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.LAST_NAME);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.DOB);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.GENDER);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.UNIQUE_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.RELATIONAL_ID);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.PHONE_NUMBER);
        columnList.add(Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.family.util.DBConstants.KEY.OTHER_PHONE_NUMBER);
        columnList.add("T2." + org.smartregister.family.util.DBConstants.KEY.PHONE_NUMBER + " AS " + org.smartregister.chw.tb.util.DBConstants.Key.FAMILY_HEAD_PHONE_NUMBER);
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + org.smartregister.family.util.DBConstants.KEY.VILLAGE_TOWN);
        columnList.add("T1." + org.smartregister.family.util.DBConstants.KEY.FIRST_NAME + " || " + "' '" + " || " + "T1." + org.smartregister.family.util.DBConstants.KEY.MIDDLE_NAME + " || " + "' '" + " || " + "T1." + org.smartregister.family.util.DBConstants.KEY.LAST_NAME + " AS " + org.smartregister.family.util.DBConstants.KEY.PRIMARY_CAREGIVER);
        columnList.add("T2." + org.smartregister.family.util.DBConstants.KEY.FIRST_NAME + " || " + "' '" + " || " + "T2." + org.smartregister.family.util.DBConstants.KEY.MIDDLE_NAME + " || " + "' '" + " || " + "T2." + org.smartregister.family.util.DBConstants.KEY.LAST_NAME + " AS " + org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD);
        columnList.add(Constants.TABLE_NAME.FAMILY + "." + org.smartregister.family.util.DBConstants.KEY.FIRST_NAME + " as " + org.smartregister.chw.anc.util.DBConstants.KEY.FAMILY_NAME);
        columnList.add(org.smartregister.chw.hivst.util.Constants.TABLES.HIVST_RESULTS + "." + DBConstants.KEY.KIT_CODE);
        columnList.add(org.smartregister.chw.hivst.util.Constants.TABLES.HIVST_RESULTS  + "." + DBConstants.KEY.KIT_FOR);
        columnList.add(org.smartregister.chw.hivst.util.Constants.TABLES.HIVST_RESULTS  + "." + DBConstants.KEY.HIVST_RESULT);

        return columnList.toArray(new String[columnList.size()]);

    }

}
