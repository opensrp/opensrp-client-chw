package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.FamilyOtherMemberProfileFragmentPresenter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileFragmentModel;
import org.smartregister.family.util.Constants;

import java.util.HashMap;

import timber.log.Timber;

public class FamilyOtherMemberProfileFragment extends BaseFamilyOtherMemberProfileFragment {
    private static final String TAG = FamilyOtherMemberProfileFragment.class.getCanonicalName();

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
        String baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter = new FamilyOtherMemberProfileFragmentPresenter(this, new BaseFamilyOtherMemberProfileFragmentModel(), null, familyBaseEntityId, baseEntityId);
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

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO Implement
        Timber.d(TAG, "setAdvancedSearchFormData unimplemented");
    }

}
