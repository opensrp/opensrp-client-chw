package org.smartregister.chw.view_holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.smartregister.chw.R;

public class IllnessCheckViewHolder extends BaseIllnessViewHolder {
    public LinearLayout checkboxParentLayout;

    public IllnessCheckViewHolder(View view) {
        super(view);
        checkboxParentLayout = view.findViewById(R.id.checkBoxParent);
    }
}
