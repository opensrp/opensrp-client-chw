package org.smartregister.chw.core.utils;

import android.content.Context;

import org.smartregister.chw.core.R;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {
    public static RegisterConfiguration defaultRegisterConfiguration(Context context) {
        if (context == null) {
            return null;
        }

        RegisterConfiguration config = new RegisterConfiguration();
        config.setEnableAdvancedSearch(false);
        config.setEnableFilterList(false);
        config.setEnableSortList(false);
        config.setSearchBarText(context.getString(R.string.search_name_or_id));
        config.setEnableJsonViews(false);

        List<Field> filers = new ArrayList<>();
        config.setFilterFields(filers);

        List<Field> sortFields = new ArrayList<>();
        config.setSortFields(sortFields);

        return config;
    }

}
