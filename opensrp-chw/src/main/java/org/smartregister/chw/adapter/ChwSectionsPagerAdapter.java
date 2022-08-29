package org.smartregister.chw.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.fragment.DailyTalliesFragment;
import org.smartregister.chw.core.fragment.DraftMonthlyFragment;
import org.smartregister.chw.core.fragment.SentMonthlyFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ChwSectionsPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public ChwSectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return DailyTalliesFragment.newInstance();
            case 1:
                return DraftMonthlyFragment.newInstance();
            case 2:
                return SentMonthlyFragment.newInstance();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.hia2_daily_tallies);
            case 1:
                return context.getString(R.string.hia2_draft_monthly);
            case 2:
                return context.getString(R.string.hia2_sent_monthly);
            default:
                break;
        }
        return null;
    }
}
