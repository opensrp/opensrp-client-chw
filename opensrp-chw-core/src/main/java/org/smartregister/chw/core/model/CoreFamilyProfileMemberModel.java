package org.smartregister.chw.core.model;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.model.BaseFamilyProfileMemberModel;

public class CoreFamilyProfileMemberModel extends BaseFamilyProfileMemberModel {

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = super.mainColumns(tableName);
        String[] newColumns = new String[]{
                tableName + "." + ChildDBConstants.KEY.ENTITY_TYPE,
                tableName + "." + CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER
        };

        return ArrayUtils.addAll(columns, newColumns);


    }
}