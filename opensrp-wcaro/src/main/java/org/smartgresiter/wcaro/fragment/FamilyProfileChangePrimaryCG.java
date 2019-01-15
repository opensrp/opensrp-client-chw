package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.adapter.MemberAdapter;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.presenter.FamilyChangeContractPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FamilyProfileChangePrimaryCG extends FamilyProfileChangeHead{

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
            res.put("position", "change_primary_cg");
            updateFamilyMember(res);
        }
    }
}
