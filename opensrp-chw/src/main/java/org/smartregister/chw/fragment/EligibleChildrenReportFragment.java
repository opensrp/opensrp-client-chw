package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.adapter.EligibleChildrenAdapter;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EligibleChildrenReportFragment extends ReportResultFragment<EligibleChild> {
    public static final String TAG = "EligibleChildrenReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            List<EligibleChild> list = new ArrayList<>();
            EligibleChild child = new EligibleChild();
            child.setID("12345");
            child.setDateOfBirth(new Date());
            child.setFullName("Joe Smith");
            child.setFamilyName("Smith Family");
            child.setDueVaccines(new String[]{"OPV", "Penta", "Rota"});

            list.add(child);
            list.add(child);

            return list;
        });
    }

    @NonNull
    @Override
    public ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> adapter() {
        return new EligibleChildrenAdapter(list, this);
    }

}
