package org.smartregister.chw.custom_view;

import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import timber.log.Timber;

public abstract class DefaultFamilyMemberFloatingMenuFlv implements FamilyMemberFloatingMenu.Flavor {

    @Override
    public void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone) {
        menu.setVisibility(has_phone ? View.VISIBLE : View.GONE);
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
