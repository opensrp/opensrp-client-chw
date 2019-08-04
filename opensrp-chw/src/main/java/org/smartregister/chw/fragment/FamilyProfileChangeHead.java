package org.smartregister.chw.fragment;

import android.os.Bundle;

import com.opensrp.chw.core.fragment.CoreFamilyProfileChangeHead;
import com.opensrp.chw.core.presenter.CoreFamilyChangePresenter;

import org.smartregister.chw.presenter.FamilyChangePresenter;

public class FamilyProfileChangeHead extends CoreFamilyProfileChangeHead {
    private FamilyChangePresenter familyChangePresenter;

    public static FamilyProfileChangeHead newInstance(String familyID) {
        FamilyProfileChangeHead fragment = new FamilyProfileChangeHead();
        Bundle args = new Bundle();
        args.putString(FAMILY_ID, familyID);
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
