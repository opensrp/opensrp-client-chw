package org.smartregister.chw.model;

import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_CHILD;

import org.smartregister.chw.core.model.CoreCertificationRegisterFragmentModel;
import org.smartregister.chw.util.CrvsDBUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class BirthCertificationRegisterFragmentModel extends CoreCertificationRegisterFragmentModel {

    @Override
    public String countSelect(String tableName, String mainCondition, String familyMemberTableName) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.selectInitiateMainTableCounts(tableName);
        countQueryBuilder.customJoin("INNER JOIN " + familyMemberTableName + " ON  " + tableName + ".base_entity_id =  " + familyMemberTableName + ".base_entity_id");
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, CrvsDBUtils.mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBuilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBuilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver COLLATE NOCASE ");
        queryBuilder.customJoin("LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);

        return queryBuilder.mainCondition(mainCondition);
    }


    public String outOfAreaSelect(String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(EC_OUT_OF_AREA_CHILD, CrvsDBUtils.outOfAreaMainColumns(EC_OUT_OF_AREA_CHILD));
        return queryBuilder.mainCondition(mainCondition);
    }

}
