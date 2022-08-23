package org.smartregister.chw.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class InAppReportsActivity extends SecuredActivity implements View.OnClickListener {


    protected CustomFontTextView toolBarTextView;
    protected AppBarLayout appBarLayout;
    protected ConstraintLayout cbhsReportsLayout;
    protected ConstraintLayout motherChampionReportsLayout;

    @Override
    protected void onCreation() {
        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
        setContentView(R.layout.activity_in_app_reports);
        setUpToolbar();
        setUpViews();
    }

    @Override
    protected void onResumption() {
        //overridden
    }


    public void setUpViews() {
        cbhsReportsLayout = findViewById(R.id.cbhs_summary);
        motherChampionReportsLayout = findViewById(R.id.mother_champion_reports);
        motherChampionReportsLayout.setOnClickListener(this);
        cbhsReportsLayout.setOnClickListener(this);
    }

    public void setUpToolbar() {
        Toolbar toolbar = findViewById(org.smartregister.chw.core.R.id.back_to_nav_toolbar);
        toolBarTextView = toolbar.findViewById(org.smartregister.chw.core.R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(org.smartregister.chw.core.R.drawable.ic_arrow_back_white_24dp);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        toolBarTextView.setText(R.string.reports_title);
        toolBarTextView.setOnClickListener(v -> finish());
        appBarLayout = findViewById(org.smartregister.chw.core.R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cbhs_summary) {
            Intent intent = new Intent(this, CBHSReportsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.mother_champion_reports){
            Intent intent = new Intent(this, MotherChampionReportsActivity.class);
            startActivity(intent);
        }
    }

}