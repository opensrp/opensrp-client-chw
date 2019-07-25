package com.opensrp.chw.hf.fragement;

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
}
