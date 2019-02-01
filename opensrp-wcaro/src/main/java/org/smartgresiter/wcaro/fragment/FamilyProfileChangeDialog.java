package org.smartgresiter.wcaro.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.adapter.MemberAdapter;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.presenter.FamilyChangeContractPresenter;
import org.smartgresiter.wcaro.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FamilyProfileChangeDialog extends DialogFragment implements View.OnClickListener, FamilyChangeContract.View {

    protected Context context;
    protected String familyID;
    protected String actionType;
    protected Runnable onSaveAndClose;


    protected MemberAdapter memberAdapter;
    RecyclerView recyclerView;
    FamilyChangeContract.Presenter presenter;
    List<HashMap<String, String>> members;
    TextView tvInfo;
    TextView tvTitle;
    ProgressBar progressBar;

    public static FamilyProfileChangeDialog newInstance(Context context, String familyBaseEntityId, String actionType) {
        FamilyProfileChangeDialog fragment = new FamilyProfileChangeDialog();
        fragment.setContext(context);
        fragment.setFamilyBaseEntityId(familyBaseEntityId);
        fragment.setActionType(actionType);

        return fragment;
    }

    public void setOnSaveAndClose(Runnable onClose) {
        this.onSaveAndClose = onClose;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyID = familyBaseEntityId;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_family_profile_change_dialog, container, false);
        prepareViews(root);
        members = new ArrayList<>();
        presenter = new FamilyChangeContractPresenter(this, familyID);
        if (actionType.equals(Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER)) {
            presenter.getAdultMembersExcludePCG();
        } else {
            presenter.getAdultMembersExcludeHOF();
        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (height * 2 / 3);
        root.setLayoutParams(params);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                close();
                break;
            case R.id.tvSubmit:
                validateSave(memberAdapter.getSelected());
                break;
        }
    }

    protected void prepareViews(View view) {
        view.findViewById(R.id.tvSubmit).setOnClickListener(this);
        view.findViewById(R.id.tvCancel).setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);
        tvInfo = view.findViewById(R.id.tvWarning);
        tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.rvList);
        progressBar.setVisibility(View.INVISIBLE);


        if (actionType.equals(Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER)) {
            tvTitle.setText("Select a new primary care giver");
            tvInfo.setText("Before you remove this member you must select a primary care giver");
        } else {
            tvTitle.setText("Select a new family head");
            tvInfo.setText("Before you remove this member you must select a new family head.");
        }
    }

    @Override
    public void refreshMembersView(List<HashMap<String, String>> familyMembers) {
        if (familyMembers != null) {
            members.clear();
            members.addAll(familyMembers);

            if (memberAdapter == null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                memberAdapter = new MemberAdapter(getActivity(), members, new MyListener());
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
        }
        if (StringUtils.isNotBlank(careGiverID)) {
            returnIntent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, careGiverID);
        }
        getActivity().setResult(Activity.RESULT_OK, returnIntent);

        if (onSaveAndClose != null) {
            onSaveAndClose.run();
        }
        close();
    }

    @Override
    public void updateFamilyMember(HashMap<String, String> familyMember) {
        showProgressDialog("Saving");
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
        dismiss();
    }

    protected void validateSave(int itemPosition) {
        Boolean valid = memberAdapter.validateSave((MemberAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(itemPosition));
        if (valid) {
            HashMap<String, String> res = memberAdapter.getSelectedResults(
                    (MemberAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(itemPosition),
                    itemPosition
            );
            res.put(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE, actionType);
            updateFamilyMember(res);
        }
    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer selectedItem = recyclerView.getChildLayoutPosition(view);
            if (!selectedItem.equals(memberAdapter.getSelected())) {
                memberAdapter.setSelected(selectedItem);
                memberAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = null;
                if (getDialog() != null) {
                    window = getDialog().getWindow();
                }

                if (window == null) {
                    return;
                }

                Point size = new Point();
                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                int height = size.y;

                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.9));
                window.setGravity(Gravity.CENTER);
            }
        });
    }
}
