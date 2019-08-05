package com.opensrp.chw.hf.fragement;


import android.content.Context;

import com.opensrp.chw.core.fragment.CoreFamilyProfileChangeDialog;
import com.opensrp.chw.core.presenter.CoreFamilyChangePresenter;
import com.opensrp.chw.hf.presenter.FamilyChangePresenter;


public class FamilyProfileChangeDialog extends CoreFamilyProfileChangeDialog {

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