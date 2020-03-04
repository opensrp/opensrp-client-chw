package org.smartregister.chw.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.VillageDose;

import java.util.Map;

public class VillageDoseViewHolder extends ListableViewHolder<VillageDose> {

    private View currentView;
    private TextView tvName;
    private LinearLayout linearLayout;

    public VillageDoseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.currentView = itemView;
        tvName = itemView.findViewById(R.id.tvName);
        linearLayout = itemView.findViewById(R.id.linearLayout);
    }

    @Override
    public void bindView(VillageDose villageDose, ListContract.View<VillageDose> view) {
        tvName.setText(villageDose.getVillageName());

        for (Map.Entry<String, Integer> entry : villageDose.getRecurringServices().entrySet()) {
            View viewVillage = LayoutInflater.from(currentView.getContext()).inflate(R.layout.village_dose, null, false);
            TextView tvName = viewVillage.findViewById(R.id.tvName);
            tvName.setText(entry.getKey() + ": " + entry.getValue().toString());
            linearLayout.addView(viewVillage);
        }

        currentView.setOnClickListener(v -> view.onListItemClicked(villageDose, v.getId()));
    }

    @Override
    public void resetView() {
        tvName.setText("");
        linearLayout.removeAllViews();
    }
}
