package org.smartregister.chw.fragment;

import android.content.Intent;
import android.view.View;

import android.widget.Toast;
import org.smartregister.chw.activity.MalariaProfileActivity;
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
    protected void onViewClicked(View view) {
        if (view.getId() == org.smartregister.malaria.R.id.due_button) {
            goToPatientProfile();
        } else {
            boolean cpo = view.getTag() instanceof CommonPersonObjectClient;
            Toast.makeText(getActivity().getApplicationContext(), Boolean.toString(cpo), Toast.LENGTH_LONG).show();
        }


    }

    private void goToPatientProfile() {
        Intent intent = new Intent(getActivity(), MalariaProfileActivity.class);
        intent.putExtra("full_name", "Denis Rwelamila");
        intent.putExtra("age", 26);
        intent.putExtra("gender", "Male");
        intent.putExtra("location", "Tabata");
        intent.putExtra("unique_id", "ID#1231232");
        MalariaProfileActivity.startMalariaActivity(getActivity(), intent);
    }



}


