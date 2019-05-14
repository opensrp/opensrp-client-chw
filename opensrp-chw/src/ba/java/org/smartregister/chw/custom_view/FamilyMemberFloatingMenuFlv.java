package org.smartregister.chw.custom_view;

import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FamilyMemberFloatingMenuFlv {

    private FamilyMemberFloatingMenu menu;

    public FamilyMemberFloatingMenuFlv(FamilyMemberFloatingMenu familyMemberFloatingMenu) {
        this.menu = familyMemberFloatingMenu;
    }


    public void reDraw(boolean has_phone) {
        redrawWithOption(has_phone);
    }

    private void redrawWithOption(boolean has_phone) {
        TextView callTextView = menu.findViewById(R.id.CallTextView);
        TextView callTextViewHint = menu.findViewById(R.id.CallTextViewHint);

        if (has_phone) {

            callTextViewHint.setVisibility(GONE);
            menu.getCallLayout().setOnClickListener(menu);
            callTextView.setTypeface(null, Typeface.NORMAL);
            callTextView.setTextColor(menu.getResources().getColor(android.R.color.black));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(255);

        } else {

            callTextViewHint.setVisibility(VISIBLE);
            menu.getCallLayout().setOnClickListener(null);
            callTextView.setTypeface(null, Typeface.ITALIC);
            callTextView.setTextColor(menu.getResources().getColor(R.color.grey));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(122);

        }
    }

    public void prepareFab(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.animateFAB();
            }
        });

        fab.setImageResource(R.drawable.ic_edit_white);
    }

    public void fabInteraction() {
        menu.animateFAB();
    }
}
