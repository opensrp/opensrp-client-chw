package org.smartregister.chw.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;

public class ReportViewHolder extends ListableViewHolder<ReportType> {

    private TextView tvName;

    public ReportViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
    }

    @Override
    public void bindView(ReportType reportType, ListContract.View<ReportType> view) {
        tvName.setText(reportType.getName());
        tvName.setOnClickListener(v -> view.onListItemClicked(reportType, R.id.tvName));
    }

    @Override
    public void resetView() {
        tvName.setText("");
    }
}
