package org.smartregister.chw.model;

import org.smartregister.chw.core.model.CoreChildRegisterFragmentModel;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class ChildRegisterFragmentModel extends CoreChildRegisterFragmentModel {

    @Override
    public String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, CoreChildUtils.mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver COLLATE NOCASE ");
        queryBUilder.customJoin("LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);

        return queryBUilder.mainCondition(mainCondition);
    }
}
