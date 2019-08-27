package org.smartregister.chw.activity;

import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import org.smartregister.chw.fragment.JobAidsDashboardFragment;
import org.smartregister.chw.fragment.JobAidsGuideBooksFragment;
import org.smartregister.chw.listener.JobsAidsBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.reporting.domain.TallyStatus;
import org.smartregister.reporting.event.IndicatorTallyEvent;

import timber.log.Timber;

public class JobAidsActivity extends FamilyRegisterActivity {

    private static final String REPORT_LAST_PROCESSED_DATE = "REPORT_LAST_PROCESSED_DATE";
    private ViewPager mViewPager;
    private ImageView refreshIndicatorsIcon;
    private ProgressBar refreshIndicatorsProgressBar;

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Handle Indicator Tallying complete event from reporting lib
     * When done tallying counts, update view
     *
     * @param event The Indicator tally event we're handling
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIndicatorTallyingComplete(IndicatorTallyEvent event) {
        if (event.getStatus() == TallyStatus.COMPLETE) {
            if (mViewPager != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
            refreshIndicatorsProgressBar.setVisibility(View.GONE);
            refreshIndicatorsIcon.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), getString(R.string.indicators_updating_complete), Toast.LENGTH_LONG).show();
        }
    }

    public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return JobAidsDashboardFragment.newInstance();
                case 1:
                    return JobAidsGuideBooksFragment.newInstance();
                default:
                    return JobAidsDashboardFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof JobAidsDashboardFragment) {
                ((JobAidsDashboardFragment) object).loadIndicatorTallies();
            }
            return super.getItemPosition(object);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_aids);
        setUpView();
        registerBottomNavigation();
    }

    private void setUpView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        refreshIndicatorsIcon = findViewById(R.id.refreshIndicatorsIcon);
        refreshIndicatorsProgressBar = findViewById(R.id.refreshIndicatorsPB);
        // Initial view until we determined by the refresh function
        refreshIndicatorsProgressBar.setVisibility(View.GONE);

        refreshIndicatorsIcon.setOnClickListener(view -> {
            refreshIndicatorsIcon.setVisibility(View.GONE);
            FadingCircle circle = new FadingCircle();
            refreshIndicatorsProgressBar.setIndeterminateDrawable(circle);
            refreshIndicatorsProgressBar.setVisibility(View.VISIBLE);
            refreshIndicatorData();
        });
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {

            bottomNavigationView.getMenu().add(Menu.NONE, org.smartregister.R.string.action_me, Menu.NONE, org.smartregister.R.string.me)
                    .setIcon(bottomNavigationHelper
                            .writeOnDrawable(org.smartregister.R.drawable.bottom_bar_initials_background, "", getResources()));
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            bottomNavigationView.getMenu().removeItem(org.smartregister.R.string.action_me);

            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setSelectedItemId(org.smartregister.family.R.id.action_job_aids);

            JobsAidsBottomNavigationListener childBottomNavigationListener = new JobsAidsBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(childBottomNavigationListener);

        }

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
    }

    /**
     * Refresh the indicator data by scheduling the IndicatorGeneratingJob immediately
     */
    public void refreshIndicatorData() {
        // Compute everything afresh. Last processed date is set to null to avoid messing with the processing timeline
        ChwApplication.getInstance().getContext().allSharedPreferences().savePreference(REPORT_LAST_PROCESSED_DATE, null);
        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
        Timber.d("ChwIndicatorGeneratingJob scheduled immediately to compute latest counts...");
        Toast.makeText(getApplicationContext(), getString(R.string.indicators_udpating), Toast.LENGTH_LONG).show();
    }


}
