package org.smartregister.chw.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.VillageDose;

public class VillageDoseViewHolder extends ListableViewHolder<VillageDose> {

    private View currentView;

    public VillageDoseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.currentView = itemView;
    }

    @Override
    public void bindView(VillageDose villageDose, ListContract.View<VillageDose> view) {

    }

    @Override
    public void resetView() {

    }
}
