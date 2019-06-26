package org.smartregister.chw.activity;

import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.fragment.JobAidsDashboardFragment;
import org.smartregister.chw.fragment.JobAidsGuideBooksFragment;
import org.smartregister.chw.job.ChwIndicatorGeneratingJob;
import org.smartregister.chw.listener.JobsAidsBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;

public class JobAidsActivity extends FamilyRegisterActivity {

    private static final String REPORT_LAST_PROCESSED_DATE = "REPORT_LAST_PROCESSED_DATE";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_aids);
        setUpView();
        registerBottomNavigation();
    }

    private void setUpView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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
     * Refresh the indicator data by clearing the view and scheduling the IndicatorGeneratingJob immediately
     * then triggering the reloading of the Fragment
     */
    public void refreshIndicatorData() {
        // Compute everything afresh. LPD is set to null to avoid messing with the processing timeline
        ChwApplication.getInstance().getContext().allSharedPreferences().savePreference(REPORT_LAST_PROCESSED_DATE, null);
        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
        if (mViewPager != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
        Log.d(TAG, "Refreshing indicators...");
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
}
