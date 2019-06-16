package org.smartregister.chw.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
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
    protected void onViewClicked(android.view.View view) {
        if (view.getId() == org.smartregister.malaria.R.id.due_button) {
            goToPatientProfile(getActivity());
        }

    }

    private void goToPatientProfile(Activity activity) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra("full_name", "Denis Rwelamila");
        intent.putExtra("age", 26);
        intent.putExtra("gender", "Male");
        intent.putExtra("location", "Tabata");
        intent.putExtra("unique_id", "ID#1231232");
        getActivity().startActivity(intent);
    }


}


