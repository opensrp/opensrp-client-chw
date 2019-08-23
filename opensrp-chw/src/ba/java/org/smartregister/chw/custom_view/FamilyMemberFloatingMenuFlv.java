package org.smartregister.chw.custom_view;

import android.support.design.widget.FloatingActionButton;

import org.smartregister.chw.R;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public class FamilyMemberFloatingMenuFlv implements FamilyMemberFloatingMenu.Flavor {

    @Override
    public void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone) {
        redrawWithOption(menu, has_phone);
    }

    @Override
    public void prepareFab(final FamilyMemberFloatingMenu menu, FloatingActionButton fab) {
        fab.setOnClickListener(v -> menu.animateFAB());

        fab.setImageResource(R.drawable.ic_edit_white);
    }

    @Override
    public void fabInteraction(FamilyMemberFloatingMenu menu) {
        menu.animateFAB();
    }
}
