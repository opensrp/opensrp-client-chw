package org.smartregister.chw.fragment;

import android.os.Bundle;

import org.smartregister.chw.core.fragment.CoreFamilyOtherMemberProfileFragment;
import org.smartregister.chw.presenter.FamilyOtherMemberProfileFragmentPresenter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileFragmentModel;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

public class FamilyOtherMemberProfileFragment extends CoreFamilyOtherMemberProfileFragment {

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
    protected BaseRegisterFragmentContract.Presenter getFamilyOtherMemberProfileFragmentPresenter(String familyBaseEntityId, String baseEntityId) {
        return new FamilyOtherMemberProfileFragmentPresenter(this, new BaseFamilyOtherMemberProfileFragmentModel(), null, familyBaseEntityId, baseEntityId);
    }
}
