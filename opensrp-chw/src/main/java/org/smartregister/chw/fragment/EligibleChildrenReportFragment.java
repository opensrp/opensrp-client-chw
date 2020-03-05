package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.adapter.EligibleChildrenAdapter;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.viewholder.ListableViewHolder;

public class EligibleChildrenReportFragment extends ReportResultFragment<EligibleChild> {
    public static final String TAG = "EligibleChildrenReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> ReportDao.eligibleChildrenReport(communityID, reportDate));
    }

    @NonNull
    @Override
    public ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> adapter() {
        return new EligibleChildrenAdapter(list, this);
    }

}
