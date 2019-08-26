package org.smartregister.chw.custom_view;

import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.R;

public class FamilyMemberFloatingMenuFlv implements FamilyMemberFloatingMenu.Flavor {

    @Override
    public void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone) {
        redrawWithOption(menu, has_phone);
    }

    private void redrawWithOption(FamilyMemberFloatingMenu menu, boolean has_phone) {
        TextView callTextView = menu.findViewById(R.id.CallTextView);
        TextView callTextViewHint = menu.findViewById(R.id.CallTextViewHint);

        if (has_phone) {

            callTextViewHint.setVisibility(View.GONE);
            menu.getCallLayout().setOnClickListener(menu);
            callTextView.setTypeface(null, Typeface.NORMAL);
            callTextView.setTextColor(menu.getResources().getColor(android.R.color.black));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(255);

        } else {

            callTextViewHint.setVisibility(View.VISIBLE);
            menu.getCallLayout().setOnClickListener(null);
            callTextView.setTypeface(null, Typeface.ITALIC);
            callTextView.setTextColor(menu.getResources().getColor(R.color.grey));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(122);

        }
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
