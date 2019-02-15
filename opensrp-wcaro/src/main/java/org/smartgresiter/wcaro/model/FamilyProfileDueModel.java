package org.smartgresiter.wcaro.model;

import org.apache.commons.lang3.ArrayUtils;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartregister.family.model.BaseFamilyProfileDueModel;

public class FamilyProfileDueModel extends BaseFamilyProfileDueModel {

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = super.mainColumns(tableName);
        String[] newColumns = new String[]{
                tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT,
                tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE,
        };

        return ArrayUtils.addAll(columns, newColumns);
    }
}
