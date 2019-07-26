package com.opensrp.chw.hf.fragement;

import android.view.View;

import com.opensrp.chw.core.fragment.CoreFamilyRegisterFragment;
import com.opensrp.hf.R;

public class FamilyRegisterFragment extends CoreFamilyRegisterFragment {

    @Override
    public void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getId() == R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        dueOnlyLayout.setVisibility(View.GONE);
    }
}
