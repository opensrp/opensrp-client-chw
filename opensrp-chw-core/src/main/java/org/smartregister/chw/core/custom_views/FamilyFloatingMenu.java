package org.smartregister.chw.core.custom_views;

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

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

public class FamilyFloatingMenu extends LinearLayout implements View.OnClickListener {
    private RelativeLayout activityMain;
    private FloatingActionButton fab;
    private LinearLayout menuBar;
    private Animation fabOpen, fabClose, rotateForward, rotateBack;
    private boolean isFabMenuOpen = false;
    private OnClickFloatingMenu onClickFloatingMenu;

    private View callLayout;
    private View addNewMember;

    public FamilyFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_family_floating_menu, this);
        activityMain = findViewById(R.id.activity_main);
        menuBar = findViewById(R.id.menu_bar);
        fab = findViewById(R.id.fab);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotateBack = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        fab.setOnClickListener(v -> animateFAB());

        callLayout = findViewById(R.id.call_layout);
        callLayout.setOnClickListener(this);

        addNewMember = findViewById(R.id.add_new_member_layout);
        addNewMember.setOnClickListener(this);

        callLayout.setClickable(false);
        addNewMember.setClickable(false);

        menuBar.setVisibility(GONE);

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
            addNewMember.startAnimation(fabClose);

            callLayout.setClickable(false);
            addNewMember.setClickable(false);
            isFabMenuOpen = false;

        } else {
            activityMain.setBackgroundResource(R.color.grey_tranparent_50);

            fab.startAnimation(rotateForward);
            fab.setImageResource(R.drawable.ic_input_add);

            callLayout.startAnimation(fabOpen);
            addNewMember.startAnimation(fabOpen);

            callLayout.setClickable(true);
            addNewMember.setClickable(true);

            isFabMenuOpen = true;
        }
    }

    public FamilyFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public FamilyFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    /**
     * re-renders call menu ui
     */
    public void reDraw(boolean has_phone) {
        TextView callTextView = findViewById(R.id.CallTextView);
        TextView callTextViewHint = findViewById(R.id.CallTextViewHint);

        callTextViewHint.setVisibility(has_phone ? GONE : VISIBLE);
        callLayout.setOnClickListener(has_phone ? this : null);
        callTextView.setTypeface(null, (has_phone ? Typeface.NORMAL : Typeface.ITALIC));
        callTextView.setTextColor(getResources().getColor(has_phone ? android.R.color.black : R.color.grey));

        ((FloatingActionButton) findViewById(R.id.callFab)).getDrawable().setAlpha(has_phone ? 255 : 122);
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    public void onClick(View v) {
        onClickFloatingMenu.onClickMenu(v.getId());
        animateFAB();
    }
}
