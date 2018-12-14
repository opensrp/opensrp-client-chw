package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;

import org.smartgresiter.wcaro.model.FamilyProfileMemberModel;
import org.smartgresiter.wcaro.presenter.FamilyProfileMemberPresenter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;

public class FamilyProfileMemberFragment extends BaseFamilyProfileMemberFragment {

    public static BaseFamilyProfileMemberFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileMemberFragment fragment = new FamilyProfileMemberFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        String baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter = new FamilyProfileMemberPresenter(this, new FamilyProfileMemberModel(), null, baseEntityId);
    }
}
