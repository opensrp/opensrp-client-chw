package org.smartregister.chw.custom_view;

import android.support.design.widget.FloatingActionButton;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FamilyMemberFloatingMenuFlv {

    private FamilyMemberFloatingMenu menu;

    public FamilyMemberFloatingMenuFlv(FamilyMemberFloatingMenu familyMemberFloatingMenu) {
        this.menu = familyMemberFloatingMenu;
    }


    public void reDraw(boolean has_phone) {
        menu.setVisibility(has_phone ? VISIBLE : GONE);
    }

    public void prepareFab(FloatingActionButton fab) {
        fab.setOnClickListener(menu);
    }

    // DO NOTHING FOR WCARO
    public void fabInteraction() {
        Timber.v("fabInteraction fired");
    }
}
