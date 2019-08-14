package org.smartregister.chw.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.model.FamilyRemoveMemberModel;
import org.smartregister.chw.presenter.FamilyRemoveMemberPresenter;
import org.smartregister.chw.provider.FamilyRemoveMemberProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class FamilyRemoveMemberFragment extends BaseFamilyProfileMemberFragment implements FamilyRemoveMemberContract.View {

    public static final String DIALOG_TAG = FamilyRemoveMemberFragment.class.getSimpleName();
    boolean processingFamily = false;
    private String familyBaseEntityId;
    private String memberName;

    public static FamilyRemoveMemberFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        FamilyRemoveMemberFragment fragment = new FamilyRemoveMemberFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        FamilyRemoveMemberProvider provider = new FamilyRemoveMemberProvider(familyBaseEntityId, this.getActivity(), this.commonRepository(), visibleColumns, new RemoveMemberListener(), new FooterListener(), familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter((Cursor) null, provider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(100);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getArguments() != null) {
            familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
            String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
            String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
            presenter = new FamilyRemoveMemberPresenter(this, new FamilyRemoveMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
        }
    }

    public FamilyRemoveMemberContract.Presenter getPresenter() {
        return (FamilyRemoveMemberContract.Presenter) presenter;
    }

    @Override
    public void removeMember(CommonPersonObjectClient client) {
        getPresenter().removeMember(client);
    }

    @Override
    public void displayChangeFamilyHeadDialog(final CommonPersonObjectClient client, final String familyHeadID) {
        FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                org.smartregister.chw.util.Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY);
        dialog.setOnSaveAndClose(() -> {
            setFamilyHead(familyHeadID);
            refreshMemberList(FetchStatus.fetched);
            getPresenter().removeMember(client);
            refreshListView();
        });
        dialog.show(getActivity().getFragmentManager(), "FamilyProfileChangeDialogHF");
    }

    @Override
    public void displayChangeCareGiverDialog(final CommonPersonObjectClient client, final String careGiverID) {
        FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                org.smartregister.chw.util.Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);
        dialog.setOnSaveAndClose(() -> {
            setPrimaryCaregiver(careGiverID);
            refreshMemberList(FetchStatus.fetched);
            getPresenter().removeMember(client);
            refreshListView();
        });

        dialog.show(getActivity().getFragmentManager(), "FamilyProfileChangeDialogPC");
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
        // display alert
        if (getActivity() != null) {
            if (org.smartregister.chw.util.Constants.EventType.REMOVE_FAMILY.equalsIgnoreCase(removalType)) {
                Intent intent = new Intent(getActivity(), FamilyRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onEveryoneRemoved() {
        // close family and return to main register
        Intent intent = new Intent(getActivity(), FamilyRegisterActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void confirmRemove(final JSONObject form) {
        if (StringUtils.isNotBlank(memberName)) {
            FamilyRemoveMemberConfirmDialog dialog = null;
            if (processingFamily) {
                dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                        String.format(getContext().getString(R.string.remove_warning_family), memberName, memberName)
                );

            } else {
                dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                        String.format(getContext().getString(R.string.confirm_remove_text), memberName)
                );
            }
            dialog.setContext(getContext());
            dialog.show(getFragmentManager(), FamilyRemoveMemberFragment.DIALOG_TAG);
            dialog.setOnRemove(() -> getPresenter().processRemoveForm(form));
        }
    }

    public void refreshMemberList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (fetchStatus.equals(FetchStatus.fetched)) {
                refreshListView();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                if (fetchStatus.equals(FetchStatus.fetched)) {
                    refreshListView();
                }

            });
        }

    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.v(DIALOG_TAG, "setAdvancedSearchFormData");
    }

    public class RemoveMemberListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(android.view.View v) {
            if (v.getTag(R.id.VIEW_ID) == BaseFamilyProfileMemberFragment.CLICK_VIEW_NEXT_ARROW ||
                    v.getTag(R.id.VIEW_ID) == BaseFamilyProfileMemberFragment.CLICK_VIEW_NORMAL) {
                final CommonPersonObjectClient pc = (CommonPersonObjectClient) v.getTag();

                memberName = String.format("%s %s %s", pc.getColumnmaps().get(DBConstants.KEY.FIRST_NAME),
                        pc.getColumnmaps().get(DBConstants.KEY.MIDDLE_NAME),
                        pc.getColumnmaps().get(DBConstants.KEY.LAST_NAME));

                String dod = pc.getColumnmaps().get(DBConstants.KEY.DOD);

                if (StringUtils.isBlank(dod)) {
                    processingFamily = false;
                    removeMember(pc);
                }
            }
        }
    }

    public class FooterListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(final android.view.View v) {
            processingFamily = true;
            HashMap<String, String> payload = (HashMap<String, String>) v.getTag();
            String message = payload.get("message");
            memberName = payload.get("name");
            closeFamily(String.format(getString(R.string.family), memberName), message);
        }
    }

}
