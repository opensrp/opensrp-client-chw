package org.smartgresiter.wcaro.custom_view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;

public class IndividualMemberFloatingMenu extends LinearLayout implements View.OnClickListener {
    private RelativeLayout activityMain;
    private FloatingActionButton fab;
    private LinearLayout menuBar;
    private Animation fab_open, fab_close;
    private boolean isFabMenuOpen = false;
    private OnClickFloatingMenu onClickFloatingMenu;
    public IndividualMemberFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    public IndividualMemberFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public IndividualMemberFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }
    private void initUi(){
        inflate(getContext(),R.layout.view_individual_floating_menu,this);
        activityMain=findViewById(R.id.activity_main);
        menuBar=findViewById(R.id.menu_bar);
        fab = findViewById(R.id.fab);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabMenuOpen)
                    collapseFabMenu();
                else
                    expandFabMenu();
            }
        });
        ((RelativeLayout)findViewById(R.id.call_layout)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.registration_layout)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.remove_member_layout)).setOnClickListener(this);
    }
    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu){
        this.onClickFloatingMenu=onClickFloatingMenu;
    }
    private void expandFabMenu() {
        activityMain.setBackgroundResource(R.color.black_tranparent_50);
        ViewCompat.animate(fab).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        menuBar.setVisibility(VISIBLE);
        menuBar.startAnimation(fab_open);
        isFabMenuOpen = true;
        fab.setImageResource(R.drawable.ic_input_add);

    }
    private void collapseFabMenu() {
        activityMain.setBackgroundResource(R.color.transparent);
        ViewCompat.animate(fab).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        menuBar.setVisibility(INVISIBLE);
        menuBar.startAnimation(fab_close);
        isFabMenuOpen = false;
        fab.setImageResource(R.drawable.ic_edit_white);

    }

    @Override
    public void onClick(View v) {
        onClickFloatingMenu.onClickMenu(v.getId());

    }
}
