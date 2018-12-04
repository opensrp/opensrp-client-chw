package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;

import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;

public class FamilyProfileDueFragment extends BaseFamilyProfileDueFragment {

    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new FamilyProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

}
