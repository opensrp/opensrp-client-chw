package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;
import android.view.View;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.model.FamilyOtherMemberProfileFragmentModel;
import org.smartgresiter.wcaro.presenter.FamilyOtherMemberProfileFragmentPresenter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.util.Constants;

public class FamilyOtherMemberProfileFragment extends BaseFamilyOtherMemberProfileFragment {
    public static BaseFamilyOtherMemberProfileFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyOtherMemberProfileFragment fragment = new FamilyOtherMemberProfileFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        presenter = new FamilyOtherMemberProfileFragmentPresenter(this, new FamilyOtherMemberProfileFragmentModel(), null, familyBaseEntityId);
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.patient_column:
                if (view.getTag() != null) { // && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }
}
