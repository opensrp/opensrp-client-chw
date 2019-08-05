package com.opensrp.chw.hf.activity;

import com.opensrp.chw.core.activity.CoreFamilyRemoveMemberActivity;
import com.opensrp.chw.hf.fragement.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity{

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
