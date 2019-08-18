package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.ChildDBConstants;

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

    @Override
    public String[] getOneYearVaccines() {
        return new String[]{"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mcv1",
                "yellowfever"
        };
    }

    @Override
    public String[] getTwoYearVaccines() {
        return new String[]{"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mcv1",
                "yellowfever", "mcv2"};
    }
}
