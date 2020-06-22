package org.smartregister.chw.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.EligibleChild;
import org.smartregister.chw.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyCommunityActivityDetailsViewHolder extends ListableViewHolder<EligibleChild> {
    protected View currentView;
    protected TextView tvName;
    protected TextView tvAge;
    protected TextView tvFamily;

    public MyCommunityActivityDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.currentView = itemView;
        tvName = itemView.findViewById(R.id.tvName);
        tvAge = itemView.findViewById(R.id.tvAge);
        tvFamily = itemView.findViewById(R.id.tvFamily);
    }

    @Override
    public void bindView(EligibleChild eligibleChild, ListContract.View<EligibleChild> view) {
        tvName.setText(eligibleChild.getFullName());
        String dob = Utils.getDuration(
                new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(eligibleChild.getDateOfBirth())
        );

        String age = currentView.getContext().getString(R.string.age);
        tvAge.setText(age + " " + dob);
        tvFamily.setText(eligibleChild.getFamilyName());
        currentView.setOnClickListener(v -> view.onListItemClicked(eligibleChild, v.getId()));
    }

    @Override
    public void resetView() {
        tvName.setText("");
        tvAge.setText("");
        tvFamily.setText("");
   }
}
