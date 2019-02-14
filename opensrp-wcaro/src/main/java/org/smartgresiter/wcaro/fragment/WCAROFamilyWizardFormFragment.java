package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.family.fragment.FamilyWizardFormFragment;

public class WCAROFamilyWizardFormFragment extends FamilyWizardFormFragment {

    public static WCAROFamilyWizardFormFragment getFormFragment(String stepName) {
        WCAROFamilyWizardFormFragment jsonFormFragment = new WCAROFamilyWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateVisibilityOfNextAndSave(false,false);
    }
}
