package org.smartregister.chw.hf.model;

import org.smartregister.chw.core.model.CoreAncRegisterFragmentModel;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;

import java.util.HashSet;
import java.util.Set;

public class AncRegisterFragmentModel extends CoreAncRegisterFragmentModel {
    @Override
    protected String[] mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();

        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + ChwDBConstants.LMP);
        columnList.add(CoreConstants.TABLE_NAME.ANC_MEMBER_LOG + "." + org.smartregister.chw.anc.util.DBConstants.KEY.DATE_CREATED);
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.CONFIRMED_VISITS);
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.LAST_HOME_VISIT);
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.PHONE_NUMBER);
        columnList.add(tableName + "." + ChwDBConstants.VISIT_NOT_DONE);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.LAST_MENSTRUAL_PERIOD);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.VILLAGE_TOWN);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FAMILY_HEAD);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.PRIMARY_CAREGIVER);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FIRST_NAME + " as " + org.smartregister.chw.anc.util.DBConstants.KEY.FAMILY_NAME);

        return columnList.toArray(new String[columnList.size()]);
    }
}
