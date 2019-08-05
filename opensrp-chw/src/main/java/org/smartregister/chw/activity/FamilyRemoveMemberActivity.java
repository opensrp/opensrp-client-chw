package org.smartregister.chw.activity;

import com.opensrp.chw.core.activity.CoreFamilyRemoveMemberActivity;

import org.smartregister.chw.fragment.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity{

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
