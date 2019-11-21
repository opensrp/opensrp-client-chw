package org.smartregister.brac.hnpp.activity;


import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.fragment.HnppDashBoardFragment;
import org.smartregister.view.activity.SecuredActivity;

public class DashBoardActivity extends SecuredActivity {

    private HnppDashBoardFragment dashBoardFragment;
    private ImageView refreshIndicatorsIcon;
    private ProgressBar refreshIndicatorsProgressBar;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_dashboard);
        setUpView();
        initializeFragment();

    }
    private void initializeFragment(){
        dashBoardFragment = new HnppDashBoardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, dashBoardFragment);
        fragmentTransaction.commit();
    }
    private void setUpView() {
        refreshIndicatorsIcon = findViewById(R.id.refreshIndicatorsIcon);
        refreshIndicatorsProgressBar = findViewById(R.id.refreshIndicatorsPB);
        // Initial view until we determined by the refresh function
        refreshIndicatorsProgressBar.setVisibility(View.GONE);

        refreshIndicatorsIcon.setOnClickListener(view -> {
            refreshIndicatorsIcon.setVisibility(View.GONE);
            FadingCircle circle = new FadingCircle();
            refreshIndicatorsProgressBar.setIndeterminateDrawable(circle);
            refreshIndicatorsProgressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> dashBoardFragment.refreshData(() -> {
                refreshIndicatorsProgressBar.setVisibility(View.GONE);
                refreshIndicatorsIcon.setVisibility(View.VISIBLE);
            }),500);

        });
    }
    @Override
    protected void onResumption() {
        if(refreshIndicatorsProgressBar !=null && refreshIndicatorsProgressBar.getVisibility() == View.VISIBLE){
            refreshIndicatorsProgressBar.setVisibility(View.GONE);
        }

    }
}
