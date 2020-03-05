package org.smartregister.chw.view_holder;

import android.view.View;
import android.widget.RadioGroup;

import org.smartregister.chw.R;

public class IllnessRadioViewHolder extends BaseIllnessViewHolder {
    public RadioGroup rgOptions;

    public IllnessRadioViewHolder(View view) {
        super(view);
        rgOptions = view.findViewById(R.id.rgOptions);
    }
}
