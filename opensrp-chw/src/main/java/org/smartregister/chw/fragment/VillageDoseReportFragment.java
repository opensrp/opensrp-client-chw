package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.VillageDoseAdapter;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.model.FilterReportFragmentModel;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rkodev
 */
public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            boolean includeAll = communityNames.get(0).equals("All communities");
            FilterReportFragmentModel model = new FilterReportFragmentModel();
            List<VillageDose> result = new ArrayList<>(ReportDao.fetchLiveVillageDosesReport(communityIds, reportDate, includeAll,
                    includeAll ? communityNames.get(0) : null, model.getAllLocations()));

            return result;
        });
    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this, this.getContext());
    }
}
