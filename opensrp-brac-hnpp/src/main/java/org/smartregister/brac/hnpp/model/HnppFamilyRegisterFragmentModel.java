package org.smartregister.brac.hnpp.model;


import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.model.BaseFamilyRegisterFramentModel;
import org.smartregister.family.util.DBConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HnppFamilyRegisterFragmentModel extends BaseFamilyRegisterFramentModel {
    @Override
    protected String[] mainColumns(String tableName) {
        String[] column = super.mainColumns(tableName);
        Set<String> columnList = new HashSet<>(Arrays.asList(column));
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.PHONE_NUMBER);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.TOTAL_MEMBER);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.CLASTER);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.VILLAGE_NAME);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.SS_NAME);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.SERIAL_NO);
        columnList.add(CoreConstants.TABLE_NAME.FAMILY + "." + HnppConstants.KEY.MODULE_ID);
        return columnList.toArray(new String[columnList.size()]);
    }
}
