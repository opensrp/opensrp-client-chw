package org.smartregister.chw.hf.activity;

import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.chw.hf.fragment.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity {

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
