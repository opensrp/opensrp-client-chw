package org.smartregister.chw.model;

import org.smartregister.chw.anc.repository.VisitRepository;
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
        countQueryBuilder.customJoin("INNER JOIN " + VisitRepository.VISIT_TABLE + " ON  " + VisitRepository.VISIT_TABLE + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBUilder.customJoin("INNER JOIN " + VisitRepository.VISIT_TABLE + " ON  " + VisitRepository.VISIT_TABLE + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        return queryBUilder.mainCondition(mainCondition);
    }

    @Override
    protected String[] mainColumns(String tableName) {
        return new String[]{
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.FIRST_NAME,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.MIDDLE_NAME,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_NAME,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.UNIQUE_ID,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.GENDER,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB,
                Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOD,
                VisitRepository.VISIT_TABLE + "." + ChildDBConstants.KEY.VISIT_TYPE,
                VisitRepository.VISIT_TABLE + "." + ChildDBConstants.KEY.VISIT_DATE,
        };
    }

}
