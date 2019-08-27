package org.smartregister.chw.core.fragment;

import android.view.View;

import org.smartregister.chw.core.R;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.HashMap;

import timber.log.Timber;

public abstract class CoreFamilyOtherMemberProfileFragment extends BaseFamilyOtherMemberProfileFragment {

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter = getFamilyOtherMemberProfileFragmentPresenter(familyBaseEntityId, baseEntityId);
    }

    protected abstract BaseRegisterFragmentContract.Presenter getFamilyOtherMemberProfileFragmentPresenter(String familyBaseEntityId, String baseEntityId);

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO Implement
        Timber.d("setAdvancedSearchFormData unimplemented");
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        if (view.getId() == R.id.patient_column && view.getTag() != null && getActivity() != null) {
            getActivity().finish();
        }
    }

}
