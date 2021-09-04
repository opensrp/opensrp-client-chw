package org.smartregister.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.VillageDose;
import org.smartregister.chw.viewholder.ListableViewHolder;
import org.smartregister.chw.viewholder.VillageDoseViewHolder;

import java.util.List;

public class VillageDoseAdapter extends ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> {
   private Context context;
    public VillageDoseAdapter(List<VillageDose> items, ListContract.View<VillageDose> view, Context context) {
        super(items, view);
        this.context = context;
    }

    @NonNull
    @Override
    public ListableViewHolder<VillageDose> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.village_dose_report_item, parent, false);
        return new VillageDoseViewHolder(view, context);
    }
}
