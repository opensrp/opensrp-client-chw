package org.smartregister.chw.hf.fragment;

import android.os.Bundle;

import org.smartregister.chw.core.fragment.CoreFamilyProfileChangeHead;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import org.smartregister.chw.hf.presenter.FamilyChangePresenter;


public class FamilyProfileChangeHead extends CoreFamilyProfileChangeHead {
    private FamilyChangePresenter familyChangePresenter;

    public FamilyProfileChangeHead() {
        phoneNumberLengthFlavor = null;
    }

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
