package org.smartregister.chw.custom_view;

import android.support.design.widget.FloatingActionButton;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class DefaultFamilyMemberFloatingMenuFlv implements FamilyMemberFloatingMenu.Flavor {

    @Override
    public void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone) {
        menu.setVisibility(has_phone ? VISIBLE : GONE);
    }

    @Override
    public void prepareFab(FamilyMemberFloatingMenu menu, FloatingActionButton fab) {
        fab.setOnClickListener(menu);
    }

    @Override
    public void fabInteraction(FamilyMemberFloatingMenu menu) {
        Timber.v("fabInteraction fired");
    }
}
