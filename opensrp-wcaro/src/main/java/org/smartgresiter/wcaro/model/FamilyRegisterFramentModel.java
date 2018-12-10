package org.smartgresiter.wcaro.model;

import org.smartgresiter.wcaro.util.ConfigHelper;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.family.model.BaseFamilyRegisterFramentModel;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

/**
 * Created by keyman on 12/07/2018.
 */
public class FamilyRegisterFramentModel extends BaseFamilyRegisterFramentModel {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(Utils.context().applicationContext());
    }

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.UNIQUE_ID +" as " +"relationalid",
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.UNIQUE_ID,
                tableName + "." + DBConstants.KEY.DOB};
        return columns;
    }
}
