package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.viewholder.EligibleChildrenViewHolder;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.List;

public class EligibleChildrenAdapter extends ListableAdapter<EligibleChild, ListableViewHolder<EligibleChild>> {

    public EligibleChildrenAdapter(List<EligibleChild> items, ListContract.View<EligibleChild> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<EligibleChild> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eligible_children_report_item, parent, false);
        return new EligibleChildrenViewHolder(view);
    }
}
