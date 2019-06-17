package org.smartregister.chw.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
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
    protected void onViewClicked(android.view.View view) {
        if (view.getTag() instanceof CommonPersonObjectClient && view.getId() == org.smartregister.malaria.R.id.due_button) {
            Toast.makeText(getActivity().getApplicationContext(), "CLICKED", Toast.LENGTH_LONG).show();
            goToPatientProfile(getActivity(), (CommonPersonObjectClient) view.getTag());
        } else {
            Boolean cpo = view.getTag() instanceof CommonPersonObjectClient;
            Toast.makeText(getActivity().getApplicationContext(), cpo.toString(), Toast.LENGTH_LONG).show();
        }


    }

    private void goToPatientProfile(Activity activity, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra("full_name", "Denis Rwelamila");
        intent.putExtra("age", 26);
        intent.putExtra("gender", "Male");
        intent.putExtra("location", "Tabata");
        intent.putExtra("unique_id", "ID#1231232");
        intent.putExtra("client", client);
        getActivity().startActivity(intent);
    }


}


