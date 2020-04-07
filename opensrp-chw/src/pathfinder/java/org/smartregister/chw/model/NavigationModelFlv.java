package org.smartregister.chw.model;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationModelFlv implements NavigationModel.Flavor {

    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption op6 = new NavigationOption(R.mipmap.sidemenu_fp, R.mipmap.sidemenu_fp_active, R.string.menu_family_planing, Constants.DrawerMenu.FAMILY_PLANNING, 0);
            NavigationOption op8 = new NavigationOption(R.mipmap.sidemenu_referrals, R.mipmap.sidemenu_referrals_active, R.string.menu_referrals, Constants.DrawerMenu.REFERRALS, 0);

            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH)
                navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_clients, CoreConstants.DrawerMenu.ALL_CLIENTS, 0));

            navigationOptions.addAll(Arrays.asList(op1, op6));

            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH)
                navigationOptions.add(op8);
        }

        return navigationOptions;
    }
}
