package org.smartregister.chw.util;

import java.util.ArrayList;

public class ChildUtilsFlv implements ChildUtils.Flavor {
    @Override
    public ArrayList<String> mainColumns(String tableName, String familyTable, String familyMemberTable) {
        ArrayList<String> columnList = new ArrayList<>();

        columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER);
        columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER_NUMBER);
        columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER_OTHER);
        columnList.add(tableName + "." + ChildDBConstants.KEY.TYPE_OF_DISABILITY);
        columnList.add(tableName + "." + ChildDBConstants.KEY.RHC_CARD);
        columnList.add(tableName + "." + ChildDBConstants.KEY.NUTRITION_STATUS);

        return columnList;
    }
}
