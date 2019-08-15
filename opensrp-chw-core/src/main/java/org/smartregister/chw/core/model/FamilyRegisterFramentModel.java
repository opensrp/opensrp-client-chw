package org.smartregister.chw.core.model;

import org.smartregister.chw.core.utils.ConfigHelper;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.family.model.BaseFamilyRegisterFramentModel;
import org.smartregister.family.util.Utils;

/**
 * Created by keyman on 12/07/2018.
 */
public class FamilyRegisterFramentModel extends BaseFamilyRegisterFramentModel {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(Utils.context().applicationContext());
    }

}
