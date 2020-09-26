package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.VillageDoseAdapter;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.List;

/**
 * @author rkodev
 */
public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            List<VillageDose> result = ReportDao.villageDosesReport(communityName, communityID, reportDate);
            if (StringUtils.isBlank(communityID))
                result.addAll(ReportDao.villageDosesReportSummary(reportDate));

            return result;
        });
    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this, this.getContext());
    }
}
