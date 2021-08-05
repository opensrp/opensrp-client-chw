package org.smartregister.chw.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.chw.interactor.FamilyRegisterJsonFormInteractor;
import org.smartregister.chw.presenter.FamilyRegisterJsonFormFragmentPresenter;

public class FamilyRegisterJsonFormFragment extends JsonWizardFormFragment {

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new FamilyRegisterJsonFormFragmentPresenter(this, FamilyRegisterJsonFormInteractor.getChildInteractorInstance());
    }

    public static FamilyRegisterJsonFormFragment getFormFragment(String stepName) {
        FamilyRegisterJsonFormFragment jsonFormFragment = new FamilyRegisterJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }
}
