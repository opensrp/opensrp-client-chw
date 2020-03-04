package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.VillageDoseAdapter;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.viewholder.ListableViewHolder;

public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {

    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this);
    }
}
