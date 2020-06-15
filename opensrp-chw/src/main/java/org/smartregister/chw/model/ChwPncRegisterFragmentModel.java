package org.smartregister.chw.model;

import org.smartregister.chw.core.model.PncRegisterFragmentModel;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class ChwPncRegisterFragmentModel extends PncRegisterFragmentModel {

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("INNER JOIN " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + " ON  " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " AND " + tableName + "." + ChwDBConstants.IS_CLOSED + " IS " + 0  + " AND " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + ChwDBConstants.IS_CLOSED + " IS " + 0 + " AND " + tableName + "." + ChwDBConstants.DELIVERY_DATE + " IS NOT NULL COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + CoreConstants.TABLE_NAME.FAMILY + " ON  " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID + " = " + CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + CoreConstants.TABLE_NAME.ANC_MEMBER + " ON  " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + CoreConstants.TABLE_NAME.ANC_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " AND " + tableName + "." + ChwDBConstants.IS_CLOSED + " IS " + 0 + " AND " + tableName + "." + ChwDBConstants.DELIVERY_DATE + " IS NOT NULL COLLATE NOCASE ");
        queryBuilder.customJoin("LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);

        return queryBuilder.mainCondition(mainCondition);
    }
}
