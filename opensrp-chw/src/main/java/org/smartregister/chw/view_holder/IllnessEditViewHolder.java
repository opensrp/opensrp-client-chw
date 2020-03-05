package org.smartregister.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.R;

public class IllnessEditViewHolder extends BaseIllnessViewHolder {
    public TextView tvValue;

    public IllnessEditViewHolder(View view) {
        super(view);
        tvValue = view.findViewById(R.id.tvValue);
    }
}
