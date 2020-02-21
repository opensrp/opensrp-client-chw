package org.smartregister.chw.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.util.Constants;

import java.util.HashMap;

public class NavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Kiswahili"};
    }

    @Override
    public HashMap<String, String> getTableMapValues() {
        HashMap<String, String> tableMap = new HashMap<>();
        tableMap.put(CoreConstants.DrawerMenu.REFERRALS, Constants.Tables.REFERRAL);
        return tableMap;
    }

    @Override
    public boolean stockReport() {
        return true;
    }

}
