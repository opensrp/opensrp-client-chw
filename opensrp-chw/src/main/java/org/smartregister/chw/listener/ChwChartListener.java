package org.smartregister.chw.listener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FragmentBaseActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.application.ChwApplicationFlv;
import org.smartregister.chw.fragment.MyCommunityActivityDetailsFragment;
import org.smartregister.chw.util.Constants;
import org.smartregister.reporting.domain.PieChartSlice;
import org.smartregister.reporting.listener.PieChartSelectListener;

public class ChwChartListener implements PieChartSelectListener {
    private Activity context;

    public ChwChartListener(Activity context) {
        this.context = context;
    }

    @Override
    public void handleOnSelectEvent(PieChartSlice pieChartSlice) {
        if (ChwApplication.getApplicationFlavor().showMyCommunityActivityReport()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ReportParameters.INDICATOR_CODE, pieChartSlice.getKey());
            FragmentBaseActivity.startMe(context, MyCommunityActivityDetailsFragment.TAG, context.getString(R.string.children), bundle);

        } else {
            Toast.makeText(context, pieChartSlice.getLabel(), Toast.LENGTH_SHORT).show();
        }
    }
}
