package org.smartregister.chw.custom_view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.listener.OnClickFloatingMenu;

public class FamilyMemberFloatingMenu extends LinearLayout implements View.OnClickListener {
    private FloatingActionButton fab;
    private OnClickFloatingMenu onClickFloatingMenu;

    public FamilyMemberFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    public FamilyMemberFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public FamilyMemberFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_individual_floating_menu, this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    public void onClick(View v) {
        onClickFloatingMenu.onClickMenu(v.getId());
    }
}
