package org.smartregister.chw.core.custom_views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.malaria.custom_views.BaseMalariaFloatingMenu;
import org.smartregister.chw.malaria.fragment.BaseMalariaCallDialogFragment;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public abstract class CoreMalariaFloatingMenu extends BaseMalariaFloatingMenu {
    public FloatingActionButton fab;
    private Animation fabOpen;
    private Animation fabClose;
    private Animation rotateForward;
    private Animation rotateBack;
    private View callLayout;
    private View referLayout;
    private RelativeLayout activityMain;
    private boolean isFabMenuOpen = false;
    private LinearLayout menuBar;
    private OnClickFloatingMenu onClickFloatingMenu;

    public CoreMalariaFloatingMenu(Context context, String clientName, String clientPhone,
                                   String clientFamilyHeadName, String clientFamilyHeadPhone) {
        super(context, clientName, clientPhone, clientFamilyHeadName, clientFamilyHeadPhone);
    }

    public CoreMalariaFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFloatMenuClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    protected void initUi() {
        inflate(getContext(), R.layout.view_malaria_floating_menu, this);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotateBack = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        activityMain = findViewById(R.id.activity_main);
        menuBar = findViewById(R.id.menu_bar);

        fab = findViewById(R.id.malaria_fab);
        fab.setOnClickListener(this);

        callLayout = findViewById(R.id.call_layout);
        callLayout.setOnClickListener(this);
        callLayout.setClickable(false);

        referLayout = findViewById(R.id.refer_to_facility_layout);
        referLayout.setOnClickListener(this);
        referLayout.setClickable(false);

        menuBar.setVisibility(GONE);

    }

    @Override
    public void onClick(View view) {
        onClickFloatingMenu.onClickMenu(view.getId());
    }

    public void animateFAB() {
        if (menuBar.getVisibility() == GONE) {
            menuBar.setVisibility(VISIBLE);
        }

        if (isFabMenuOpen) {
            activityMain.setBackgroundResource(R.color.transparent);
            fab.startAnimation(rotateBack);
            fab.setImageResource(R.drawable.ic_edit_white);

            callLayout.startAnimation(fabClose);
            callLayout.setClickable(false);

            referLayout.startAnimation(fabClose);
            referLayout.setClickable(false);
            isFabMenuOpen = false;
        } else {
            activityMain.setBackgroundResource(R.color.grey_tranparent_50);
            fab.startAnimation(rotateForward);
            fab.setImageResource(R.drawable.ic_input_add);

            callLayout.startAnimation(fabOpen);
            callLayout.setClickable(true);

            referLayout.startAnimation(fabOpen);
            referLayout.setClickable(true);
            isFabMenuOpen = true;
        }
    }


    public void launchCallWidget() {
        BaseMalariaCallDialogFragment.launchDialog((Activity) this.getContext(), getClientName(),
                getPhoneNumber(), getFamilyHeadName(), getFamilyHeadPhone());
    }

    public void redraw(boolean hasPhoneNumber) {
        redrawWithOption(this, hasPhoneNumber);
    }

    public View getCallLayout() {
        return callLayout;
    }
}
