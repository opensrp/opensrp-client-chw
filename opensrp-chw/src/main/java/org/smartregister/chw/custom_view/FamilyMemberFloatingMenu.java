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

import org.smartregister.chw.R;
import org.smartregister.chw.listener.OnClickFloatingMenu;

public class FamilyMemberFloatingMenu extends LinearLayout implements View.OnClickListener {
    private RelativeLayout activityMain;
    private FloatingActionButton fab;
    private LinearLayout menuBar;
    private Animation fabOpen, fabClose, rotateForward, rotateBack;
    private boolean isFabMenuOpen = false;
    private OnClickFloatingMenu onClickFloatingMenu;

    private View callLayout, familyDetail,removeMember;

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

    /**
     * re-renders call menu ui
     */
    public void reDraw(boolean has_phone){
        TextView callTextView =  findViewById(R.id.CallTextView);
        TextView callTextViewHint =  findViewById(R.id.CallTextViewHint);

        if(has_phone){
            callTextViewHint.setVisibility(GONE);
            callLayout.setOnClickListener(this);
            callTextView.setTypeface(null, Typeface.NORMAL);
            callTextView.setTextColor(getResources().getColor(android.R.color.black));
        }else{
            callTextViewHint.setVisibility(VISIBLE);
            callLayout.setOnClickListener(null);
            callTextView.setTypeface(null, Typeface.ITALIC);
            callTextView.setTextColor(getResources().getColor(R.color.grey));
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

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        callLayout = findViewById(R.id.call_layout);
        callLayout.setOnClickListener(this);

        familyDetail = findViewById(R.id.registration_layout);
        familyDetail.setOnClickListener(this);

        removeMember = findViewById(R.id.remove_member_layout);
        removeMember.setOnClickListener(this);

        callLayout.setClickable(false);
        familyDetail.setClickable(false);
        removeMember.setClickable(false);

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
            familyDetail.startAnimation(fabClose);
            removeMember.startAnimation(fabClose);
            callLayout.setClickable(false);
            familyDetail.setClickable(false);
            removeMember.setClickable(false);

            isFabMenuOpen = false;

        } else {
            activityMain.setBackgroundResource(R.color.black_tranparent_50);

            fab.startAnimation(rotateForward);
            fab.setImageResource(R.drawable.ic_input_add);

            callLayout.startAnimation(fabOpen);
            familyDetail.startAnimation(fabOpen);
            removeMember.startAnimation(fabOpen);
            callLayout.setClickable(true);
            familyDetail.setClickable(true);
            removeMember.setClickable(true);

            isFabMenuOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        onClickFloatingMenu.onClickMenu(v.getId());
        animateFAB();
    }
}
