package org.smartregister.chw.custom_view;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.core.custom_views.FamilyFloatingMenu;

public class ChwFamilyFloatingMenu extends FamilyFloatingMenu {

    private Flavor flavor = new FamilyFloatingMenuFlv();

    public ChwFamilyFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    @Override
    public void initUi() {
        flavor = new FamilyFloatingMenuFlv();
        super.initUi();
    }

    public void setCallLayoutVisibility() {
        flavor.setCallLayoutVisibility(callLayout);
    }


    interface Flavor {
        void setCallLayoutVisibility(View view);
    }

}
