package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.adapter.MemberAdapter;
import org.smartgresiter.wcaro.presenter.FamilyChangeContractPresenter;
import org.smartgresiter.wcaro.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class FamilyProfileChangePrimaryCG extends FamilyProfileChangeHead {

    public static FamilyProfileChangePrimaryCG newInstance(String familyID) {
        FamilyProfileChangePrimaryCG fragment = new FamilyProfileChangePrimaryCG();
        Bundle args = new Bundle();
        args.putString(FamilyProfileChangeHead.FAMILY_ID, familyID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_family_profile_change_primary_cg, container, false);
        super.prepareViews(root);
        members = new ArrayList<>();
        presenter = new FamilyChangeContractPresenter(this, this.familyID);
        presenter.getAdultMembersExcludePCG();
        return root;
    }

    @Override
    protected void validateSave(int itemPosition) {
        Boolean valid = memberAdapter.validateSave((MemberAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(itemPosition));
        if (valid) {
            HashMap<String, String> res = memberAdapter.getSelectedResults(
                    (MemberAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(itemPosition),
                    itemPosition
            );
            res.put(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE, Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);
            updateFamilyMember(res);
        }
    }
}
