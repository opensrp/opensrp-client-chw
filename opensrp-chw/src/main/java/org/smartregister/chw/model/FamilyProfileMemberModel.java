package org.smartregister.chw.model;

import com.opensrp.chw.core.utils.ChildDBConstants;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.model.BaseFamilyProfileMemberModel;

public class FamilyProfileMemberModel extends BaseFamilyProfileMemberModel {

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = super.mainColumns(tableName);
        String[] newColumns = new String[]{
                tableName + "." + ChildDBConstants.KEY.ENTITY_TYPE,
                tableName + "." + Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER
        };

        return ArrayUtils.addAll(columns, newColumns);


    }
}
