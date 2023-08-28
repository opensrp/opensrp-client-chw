package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.SbcMemberProfileActivity;
import org.smartregister.chw.core.fragment.CoreSbcRegisterFragment;
import org.smartregister.chw.model.SbcRegisterFragmentModel;
import org.smartregister.chw.sbc.presenter.BaseSbcRegisterFragmentPresenter;

public class SbcRegisterFragment extends CoreSbcRegisterFragment {
    @Override
    protected void openProfile(String baseEntityId) {
        SbcMemberProfileActivity.startMe(getActivity(), baseEntityId);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BaseSbcRegisterFragmentPresenter(this, new SbcRegisterFragmentModel(), null);
    }

}
