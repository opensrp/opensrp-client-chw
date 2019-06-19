package org.smartregister.chw.fragment;

import android.content.Intent;
import android.view.View;

import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
import org.smartregister.chw.malaria.provider.MalariaRegisterProvider;
import org.smartregister.chw.model.MalariaRegisterFragmentModel;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;

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
    protected void onViewClicked(View view) {
        if (view.getId() == org.smartregister.malaria.R.id.due_button) {
            goToClient();
        }


    }

    protected void goToClient() {
        Intent intent = new Intent(getActivity(), MalariaProfileActivity.class);
        intent.putExtra("client", MalariaRegisterProvider.getClient());
        MalariaProfileActivity.startMalariaActivity(getActivity(), intent);
    }



}


