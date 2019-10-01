package org.smartregister.brac.hnpp.model;

import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.model.CoreChildRegisterFragmentModel;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;

public class HnppAllMemberRegisterFragmentModel extends CoreChildRegisterFragmentModel {
    @Override
    public String mainSelect(String tableName, String familyName, String familyMemberName, String mainCondition) {
        return mainSelectRegisterWithoutGroupby(familyName,familyMemberName,mainCondition);
    }
    public static String mainSelectRegisterWithoutGroupby( String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(familyMemberTableName,mainColumns(familyTableName, familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");

        return queryBUilder.mainCondition(mainCondition);
    }

    public static String[] mainColumns(String familyTable, String familyMemberTable) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(familyMemberTable + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME );
        columnList.add(familyMemberTable + "." + ChildDBConstants.PHONE_NUMBER);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.RELATIONAL_ID +" as "+ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(familyMemberTable + "." +DBConstants.KEY.ENTITY_TYPE);
        columnList.add(familyTable + "." + DBConstants.KEY.VILLAGE_TOWN +" as "+HnppConstants.KEY.VILLAGE_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.GENDER);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.DOB);
        columnList.add(familyTable + "." + DBConstants.KEY.FIRST_NAME + " as " + HnppConstants.KEY.HOUSE_HOLD_NAME);

        return columnList.toArray(new String[columnList.size()]);
    }
}
