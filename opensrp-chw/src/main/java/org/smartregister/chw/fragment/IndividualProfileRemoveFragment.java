package org.smartregister.chw.fragment;

import android.content.Intent;
import android.os.Bundle;


import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.IndividualProfileRemoveActivity;
import org.smartregister.chw.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.model.FamilyRemoveMemberModel;
import org.smartregister.chw.presenter.FamilyRemoveMemberPresenter;
import org.smartregister.chw.provider.FamilyRemoveMemberProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class IndividualProfileRemoveFragment extends BaseFamilyProfileMemberFragment implements FamilyRemoveMemberContract.View {

    private static final String TAG = IndividualProfileRemoveFragment.class.getCanonicalName();

    private String familyBaseEntityId;
    private CommonPersonObjectClient pc;
    private String memberName;
    static String className;

    public static IndividualProfileRemoveFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        IndividualProfileRemoveFragment fragment = new IndividualProfileRemoveFragment();
        if (args == null) {
            args = new Bundle();
        }
        className = args.getString(org.smartregister.chw.util.Constants.INTENT_KEY.CLASS);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        FamilyRemoveMemberProvider provider = new FamilyRemoveMemberProvider(familyBaseEntityId, this.getActivity(), this.commonRepository(), visibleColumns, null, null, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(0);
        this.clientsView.setAdapter(this.clientAdapter);
        this.clientsView.setVisibility(android.view.View.GONE);
    }

    @Override
    protected void initializePresenter() {
        if (getArguments() != null) {
            familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
            String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
            String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
            pc = (CommonPersonObjectClient) getArguments().getSerializable(org.smartregister.chw.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON);
            presenter = new FamilyRemoveMemberPresenter(this, new FamilyRemoveMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
            openDeleteDialog();
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.d("setAdvancedSearchFormData");
    }

    public FamilyRemoveMemberContract.Presenter getPresenter() {
        return (FamilyRemoveMemberContract.Presenter) presenter;
    }

    @Override
    public void removeMember(CommonPersonObjectClient client) {
        getPresenter().removeMember(client);
    }

    private void openDeleteDialog() {
        memberName = String.format("%s %s %s", pc.getColumnmaps().get(DBConstants.KEY.FIRST_NAME),
                pc.getColumnmaps().get(DBConstants.KEY.MIDDLE_NAME) == null ? "" : pc.getColumnmaps().get(DBConstants.KEY.MIDDLE_NAME),
                pc.getColumnmaps().get(DBConstants.KEY.LAST_NAME) == null ? "" : pc.getColumnmaps().get(DBConstants.KEY.LAST_NAME));

        String dod = pc.getColumnmaps().get(DBConstants.KEY.DOD);
        if (StringUtils.isBlank(dod)) {
            getPresenter().removeMember(pc);
        }
    }


    public void confirmRemove(final JSONObject form) {
        if (StringUtils.isNotBlank(memberName) && getFragmentManager() != null) {
            FamilyRemoveMemberConfirmDialog dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                    String.format(getString(R.string.confirm_remove_text), memberName)
            );
            dialog.setContext(getContext());
            dialog.show(getFragmentManager(), FamilyRemoveMemberFragment.DIALOG_TAG);
            dialog.setOnRemove(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class fallbackClass = Class.forName(className);
                        getPresenter().processRemoveForm(form);
                        Intent intent = new Intent(getActivity(), className != null ? fallbackClass : AncRegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Timber.e(e);
                    }
                }
            });
            dialog.setOnRemoveActivity(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });
        }
    }


    @Override
    public void displayChangeFamilyHeadDialog(final CommonPersonObjectClient client, final String familyHeadID) {
        if (getActivity() != null && getActivity().getFragmentManager() != null) {
            FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                    org.smartregister.chw.util.Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY);
            dialog.setOnSaveAndClose(new Runnable() {
                @Override
                public void run() {
                    setFamilyHead(familyHeadID);
                    getPresenter().removeMember(client);
                }
            });
            dialog.setOnRemoveActivity(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });
            dialog.show(getActivity().getFragmentManager(), "FamilyProfileChangeDialogHF");
        }
    }

    @Override
    public void displayChangeCareGiverDialog(final CommonPersonObjectClient client, final String careGiverID) {
        if (getActivity() != null && getActivity().getFragmentManager() != null) {
            FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                    org.smartregister.chw.util.Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);
            dialog.setOnSaveAndClose(new Runnable() {
                @Override
                public void run() {
                    setPrimaryCaregiver(careGiverID);
                    getPresenter().removeMember(client);
                }
            });
            dialog.setOnRemoveActivity(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });
            dialog.show(getActivity().getFragmentManager(), "FamilyProfileChangeDialogPC");
        }
    }

    @Override
    public void closeFamily(String familyName, String details) {

        getPresenter().removeEveryone(familyName, details);

    }

    @Override
    public void goToPrevious() {
        // open family register
        startActivity(new Intent(getContext(), FamilyRegisterActivity.class));
    }

    @Override
    public void startJsonActivity(JSONObject jsonObject) {
        // Intent intent = new Intent(getContext(), Utils.metadata().familyMemberFormActivity);
        Intent intent = new Intent(getActivity(), Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonObject.toString());

        Form form = new Form();
        form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);
        form.setWizard(false);
        form.setSaveLabel("Save");
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void onMemberRemoved(String removalType) {
        if (getActivity() != null) {
            if (org.smartregister.chw.util.Constants.EventType.REMOVE_FAMILY.equalsIgnoreCase(removalType)) {
                Intent intent = new Intent(getActivity(), FamilyRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                if (getActivity() != null) {
                    if (getActivity() instanceof IndividualProfileRemoveActivity) {
                        IndividualProfileRemoveActivity p = (IndividualProfileRemoveActivity) getActivity();
                        p.onRemoveMember();
                    }
                }
            }
        }
    }

    @Override
    public void onEveryoneRemoved() {
        if (getActivity() != null && getActivity() instanceof IndividualProfileRemoveActivity) {
            IndividualProfileRemoveActivity p = (IndividualProfileRemoveActivity) getActivity();
            p.onRemoveMember();
        }
    }


    @Override
    protected String getMainCondition() {
        return "";
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

}
