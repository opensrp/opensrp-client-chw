package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;

import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;

public class FamilyProfileActivityFragment extends BaseFamilyProfileActivityFragment {
    public static BaseFamilyProfileActivityFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileActivityFragment fragment = new FamilyProfileActivityFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }
}
