package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;

import org.smartgresiter.wcaro.model.FamilyProfileDueModel;
import org.smartgresiter.wcaro.presenter.FamilyProfileDuePresenter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.util.Constants;

public class FamilyProfileDueFragment extends BaseFamilyProfileDueFragment {

    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new FamilyProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        presenter = new FamilyProfileDuePresenter(this, new FamilyProfileDueModel(), null, familyBaseEntityId);
    }
}
