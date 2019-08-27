package org.smartregister.chw.hf.custom_view;

import android.content.Context;

import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public class FamilyMemberFloatingMenu extends CoreFamilyMemberFloatingMenu {
    public FamilyMemberFloatingMenu(Context context) {
        super(context);
    }

    @Override
    public void initUi() {
        super.initUi();
        fab.setOnClickListener(v -> animateFAB());
    }

    @Override
    public void reDraw(boolean has_phone) {
        redrawWithOption(this, has_phone);
    }
}
