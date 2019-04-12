package org.smartregister.chw.custom_view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.chw.util.Country;

public class FamilyMemberFloatingMenu extends LinearLayout implements View.OnClickListener {
    private RelativeLayout activityMain;
    private FloatingActionButton fab;
    private LinearLayout menuBar;
    private Animation fabOpen, fabClose, rotateForward, rotateBack;
    private boolean isFabMenuOpen = false;
    private OnClickFloatingMenu onClickFloatingMenu;

    private View callLayout;
    private View referLayout;

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

    public void reDraw(boolean has_phone) {
        if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {

            TextView callTextView = findViewById(R.id.CallTextView);
            TextView callTextViewHint = findViewById(R.id.CallTextViewHint);

            callTextViewHint.setVisibility(has_phone ? GONE : VISIBLE);
            callLayout.setOnClickListener(has_phone ? this : null);
            callTextView.setTypeface(null, (has_phone ? Typeface.NORMAL : Typeface.ITALIC));
            callTextView.setTextColor(getResources().getColor(has_phone ? android.R.color.black : R.color.grey));

            ((FloatingActionButton) findViewById(R.id.callFab)).getDrawable().setAlpha(has_phone ? 255 : 122);

        } else {
            this.setVisibility(has_phone ? VISIBLE : GONE);
        }
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_individual_floating_menu, this);
        activityMain = findViewById(R.id.activity_main);
        menuBar = findViewById(R.id.menu_bar);
        fab = findViewById(R.id.fab);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotateBack = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            fab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateFAB();
                }
            });
        } else {
            fab.setOnClickListener(this);
        }

        if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            fab.setImageResource(R.drawable.ic_edit_white);
        }


        callLayout = findViewById(R.id.call_layout);
        callLayout.setOnClickListener(this);

        referLayout = findViewById(R.id.refer_to_facility_layout);
        referLayout.setOnClickListener(this);

        callLayout.setClickable(false);
        referLayout.setClickable(false);

        menuBar.setVisibility(GONE);
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
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
            referLayout.startAnimation(fabClose);

            callLayout.setClickable(false);
            referLayout.setClickable(false);
            isFabMenuOpen = false;

        } else {
            activityMain.setBackgroundResource(R.color.black_tranparent_50);

            fab.startAnimation(rotateForward);
            fab.setImageResource(R.drawable.ic_input_add);

            callLayout.startAnimation(fabOpen);
            referLayout.startAnimation(fabOpen);

            callLayout.setClickable(true);
            referLayout.setClickable(true);

            isFabMenuOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        onClickFloatingMenu.onClickMenu(v.getId());
        if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            animateFAB();
        }
    }
}
