package org.smartregister.brac.hnpp.custom_view;

import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;

public class HnppNavigationTopView implements NavigationMenu.FlavorTop {

    @Override
    public int getTopLogo() {
        return R.drawable.avatar_woman;
    }

    @Override
    public String topText() {
        String userName = HnppApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();

        return userName;
    }
}
