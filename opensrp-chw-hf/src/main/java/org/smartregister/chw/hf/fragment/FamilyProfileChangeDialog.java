package org.smartregister.chw.hf.fragment;


import android.content.Context;

import org.smartregister.chw.core.fragment.CoreFamilyProfileChangeDialog;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import org.smartregister.chw.hf.presenter.FamilyChangePresenter;


public class FamilyProfileChangeDialog extends CoreFamilyProfileChangeDialog {

    public FamilyProfileChangeDialog() {
        phoneNumberLengthFlavor = null;
    }

    public static CoreFamilyProfileChangeDialog newInstance(Context context, String familyBaseEntityId, String actionType) {
        CoreFamilyProfileChangeDialog fragment = new FamilyProfileChangeDialog();
        fragment.setContext(context);
        fragment.setFamilyBaseEntityId(familyBaseEntityId);
        fragment.setActionType(actionType);
        return fragment;
    }

    @Override
    protected CoreFamilyChangePresenter getPresenter() {
        return new FamilyChangePresenter(this, this.familyID);
    }
}