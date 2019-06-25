package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
import org.smartregister.chw.model.MalariaRegisterFragmentModel;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class MalariaRegisterFragment extends BaseMalariaRegisterFragment {

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new MalariaRegisterFragmentPresenter(this, new MalariaRegisterFragmentModel(), null);
    }


    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        MalariaProfileActivity.startMalariaActivity(getActivity(), new MemberObject(client), client);
    }
}


