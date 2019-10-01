package org.smartregister.brac.hnpp.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;

public class HnppNavigationMenu  implements NavigationMenu.Flavour {

    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"Bangla", "English"};
    }
}
