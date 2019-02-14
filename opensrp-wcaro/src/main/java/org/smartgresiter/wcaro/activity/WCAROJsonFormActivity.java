package org.smartgresiter.wcaro.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;

import com.vijay.jsonwizard.utils.ValidationStatus;

import com.vijay.jsonwizard.R;

import org.smartgresiter.wcaro.fragment.WCAROFamilyWizardFormFragment;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.fragment.FamilyWizardFormFragment;

public class WCAROJsonFormActivity extends FamilyWizardFormActivity {

    protected void initializeFormFragmentCore() {
        WCAROFamilyWizardFormFragment familyWizardFormFragment = WCAROFamilyWizardFormFragment.getFormFragment("step1");
        this.getSupportFragmentManager().beginTransaction().add(R.id.container, familyWizardFormFragment).commit();
    }

    @Override
    public void validateActivateNext() {
        Fragment fragment = this.getVisibleFragment();
        if (fragment != null && fragment instanceof FamilyWizardFormFragment) {
            ValidationStatus validationStatus = null;
            for (View dataView : ((FamilyWizardFormFragment) fragment).getJsonApi().getFormDataViews()) {

                validationStatus = ((FamilyWizardFormFragment) fragment).getPresenter().validate(((FamilyWizardFormFragment) fragment), dataView, false);
                if (!validationStatus.isValid()) {
                    break;
                }
            }
            if (validationStatus != null && validationStatus.isValid()) {
                ((FamilyWizardFormFragment) fragment).getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(true);

            } else {
                ((FamilyWizardFormFragment) fragment).getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
            }
        }
    }

}
