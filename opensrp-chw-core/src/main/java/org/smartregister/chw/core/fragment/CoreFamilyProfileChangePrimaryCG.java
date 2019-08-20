package org.smartregister.chw.core.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;

public abstract class CoreFamilyProfileChangePrimaryCG extends CoreFamilyProfileChangeHead {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_family_profile_change_primary_cg, container, false);
        super.prepareViews(root);
        members = new ArrayList<>();
        presenter = getPresenter();
        presenter.getAdultMembersExcludePCG();
        return root;
    }

    @Override
    protected void validateSave() {
        boolean valid = memberAdapter.validateSave();
        if (valid) {
            FamilyMember res = memberAdapter.getSelectedResults();
            if (res != null)
                updateFamilyMember(Pair.create(CoreConstants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER, res));
        }
    }
}
