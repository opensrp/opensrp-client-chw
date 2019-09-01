package org.smartregister.brac.hnpp.activity;

import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.brac.hnpp.fragment.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity {

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
