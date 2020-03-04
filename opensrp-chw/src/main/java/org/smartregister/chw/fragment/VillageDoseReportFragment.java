package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.VillageDoseAdapter;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            List<VillageDose> list = new ArrayList<>();
            VillageDose child = new VillageDose();

            child.getRecurringServices().put("OPV", 1);
            child.getRecurringServices().put("BCG", 10);
            child.getRecurringServices().put("Penta", 5);
            child.setID("12345");
            child.setVillageName("Village 1");
            list.add(child);

            return list;
        });
    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this);
    }
}
