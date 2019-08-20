package org.smartregister.chw.fragment;

import android.os.Bundle;

import org.smartregister.chw.core.fragment.CoreFamilyProfileChangeHead;
import org.smartregister.chw.core.fragment.CoreFamilyProfileChangePrimaryCG;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import org.smartregister.chw.presenter.FamilyChangePresenter;

public class FamilyProfileChangePrimaryCG extends CoreFamilyProfileChangePrimaryCG {
    private FamilyChangePresenter familyChangePresenter;

    public static CoreFamilyProfileChangePrimaryCG newInstance(String familyID) {
        CoreFamilyProfileChangePrimaryCG fragment = new FamilyProfileChangePrimaryCG();
        Bundle args = new Bundle();
        args.putString(CoreFamilyProfileChangeHead.FAMILY_ID, familyID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CoreFamilyChangePresenter getPresenter() {
        if (familyChangePresenter == null) {
            familyChangePresenter = new FamilyChangePresenter(this, this.familyID);
        }
        return familyChangePresenter;
    }
}
