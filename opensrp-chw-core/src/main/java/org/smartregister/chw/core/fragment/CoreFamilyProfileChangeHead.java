package org.smartregister.chw.core.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.MemberAdapter;
import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.listener.FloatingMenuListener;
import org.smartregister.chw.core.listener.MemberAdapterListener;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.List;

public abstract class CoreFamilyProfileChangeHead extends Fragment implements View.OnClickListener, FamilyChangeContract.View, MemberAdapterListener {

    protected static final String FAMILY_ID = "FAMILY_ID";
    protected String familyID;

    protected TextView tvAction;
    protected MemberAdapter memberAdapter;
    protected MemberAdapter.Flavor phoneNumberLengthFlavor;
    protected RecyclerView recyclerView;
    protected FamilyChangeContract.Presenter presenter;
    protected List<FamilyMember> members;
    protected ProgressBar progressBar;

    @Override
    public void onMenuChoiceChange() {
        boolean active = (memberAdapter != null && StringUtils.isNotBlank(memberAdapter.getSelected()) && memberAdapter.validateSave());
        tvAction.setTextColor(getResources().getColor(active ? R.color.white : android.R.color.darker_gray));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyID = getArguments().getString(FAMILY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_family_profile_change_head, container, false);
        prepareViews(root);
        members = new ArrayList<>();
        presenter = getPresenter();
        presenter.getAdultMembersExcludeHOF();
        return root;
    }

    protected void prepareViews(View view) {
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.tvAction).setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);
        tvAction = view.findViewById(R.id.tvAction);
        recyclerView = view.findViewById(R.id.rvList);

        progressBar.setVisibility(View.INVISIBLE);
    }

    protected abstract CoreFamilyChangePresenter getPresenter();

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close) {
            Intent returnIntent = new Intent();
            getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
            close();
        } else if (i == R.id.tvAction) {
            validateSave();
        }
    }

    protected void validateSave() {
        Boolean valid = memberAdapter.validateSave();
        if (valid) {
            FamilyMember res = memberAdapter.getSelectedResults();
            if (res != null) {
                updateFamilyMember(Pair.create(CoreConstants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY, res));
            }
        }
    }

    @Override
    public void refreshMembersView(List<FamilyMember> familyMembers) {
        if (familyMembers != null) {
            members.clear();
            members.addAll(familyMembers);

            if (memberAdapter == null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                memberAdapter = new MemberAdapter(getActivity(), members, this);
                memberAdapter.setFlavorPhoneNumberLength(phoneNumberLengthFlavor);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(memberAdapter);
            } else {
                memberAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void saveComplete(String familyHeadID, String careGiverID) {
        progressBar.setVisibility(View.INVISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Intent returnIntent = new Intent();
        if (StringUtils.isNotBlank(familyHeadID)) {
            returnIntent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyHeadID);
            FloatingMenuListener.getInstance(getActivity(), familyID);
        }
        if (StringUtils.isNotBlank(careGiverID)) {
            returnIntent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, careGiverID);
            FloatingMenuListener.getInstance(getActivity(), familyID);
        }
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        close();
    }

    @Override
    public void updateFamilyMember(Pair<String, FamilyMember> familyMember) {
        showProgressDialog(getString(R.string.status_saving));
        presenter.saveFamilyMember(getActivity(), familyMember);
    }

    @Override
    public void showProgressDialog(String title) {
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void close() {
        getActivity().finish();
    }

}
