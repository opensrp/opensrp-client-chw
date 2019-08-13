package org.smartregister.chw.core.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.opensrp.chw.core.R;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.core.provider.CoreFamilyRemoveMemberProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
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

public abstract class CoreFamilyRemoveMemberFragment extends BaseFamilyProfileMemberFragment implements FamilyRemoveMemberContract.View {
    protected CoreFamilyRemoveMemberProvider removeMemberProvider;
    protected boolean processingFamily = false;
    protected String memberName;
    protected String familyBaseEntityId;

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        setRemoveMemberProvider(visibleColumns, familyHead, primaryCaregiver, familyBaseEntityId);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, removeMemberProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(100);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void initializePresenter() {
        if (getArguments() != null) {
            String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
            String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
            familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
            setPresenter(familyHead, primaryCareGiver);
        }
    }

    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.v("setAdvancedSearchFormData");
    }

    protected abstract void setPresenter(String familyHead, String primaryCareGiver);

    public FamilyRemoveMemberContract.Presenter getPresenter() {
        return (FamilyRemoveMemberContract.Presenter) presenter;
    }

    @Override
    public void removeMember(CommonPersonObjectClient client) {
        getPresenter().removeMember(client);
    }

    @Override
    public void displayChangeFamilyHeadDialog(final CommonPersonObjectClient client, final String familyHeadID) {
        CoreFamilyProfileChangeDialog dialog = getChangeFamilyHeadDialog();
        dialog.setOnSaveAndClose(new Runnable() {
            @Override
            public void run() {
                setFamilyHead(familyHeadID);
                refreshMemberList(FetchStatus.fetched);
                getPresenter().removeMember(client);
                refreshListView();
            }
        });
        dialog.show(getActivity().getFragmentManager(), "FamilyProfileChangeDialogHF");
    }

    @Override
    public void displayChangeCareGiverDialog(final CommonPersonObjectClient client, final String careGiverID) {
        CoreFamilyProfileChangeDialog dialog = getChangeFamilyCareGiverDialog();
        dialog.setOnSaveAndClose(new Runnable() {
            @Override
            public void run() {
                setPrimaryCaregiver(careGiverID);
                refreshMemberList(FetchStatus.fetched);
                getPresenter().removeMember(client);
                refreshListView();
            }
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
        startActivity(new Intent(getContext(), getFamilyRegisterActivityClass()));
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
            if (CoreConstants.EventType.REMOVE_FAMILY.equalsIgnoreCase(removalType)) {
                Intent intent = new Intent(getActivity(), getFamilyRegisterActivityClass());
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
        Intent intent = new Intent(getActivity(), getFamilyRegisterActivityClass());
        startActivity(intent);
        getActivity().finish();
    }

    protected abstract CoreFamilyProfileChangeDialog getChangeFamilyCareGiverDialog();

    protected abstract CoreFamilyProfileChangeDialog getChangeFamilyHeadDialog();

    public void refreshMemberList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (fetchStatus.equals(FetchStatus.fetched)) {
                refreshListView();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    if (fetchStatus.equals(FetchStatus.fetched)) {
                        refreshListView();
                    }

                }
            });
        }

    }

    protected abstract void setRemoveMemberProvider(Set visibleColumns, String familyHead, String primaryCaregiver, String familyBaseEntityId);

    protected abstract Class<? extends CoreFamilyRegisterActivity> getFamilyRegisterActivityClass();

    protected abstract String getRemoveFamilyMemberDialogTag();

    public void confirmRemove(final JSONObject form) {
        if (StringUtils.isNotBlank(memberName)) {
            FamilyRemoveMemberConfirmDialog dialog;
            if (processingFamily) {
                dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                        String.format(getContext().getString(R.string.remove_warning_family), memberName, memberName)
                );

            } else {
                dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                        String.format(getContext().getString(R.string.confirm_remove_text), memberName)
                );
            }
            if (getFragmentManager() != null) {
                dialog.setContext(getContext());
                dialog.show(getFragmentManager(), getRemoveFamilyMemberDialogTag());
                dialog.setOnRemove(new Runnable() {
                    @Override
                    public void run() {
                        getPresenter().processRemoveForm(form);
                    }
                });
            }
        }
    }

    public CoreFamilyRemoveMemberProvider getProvider() {
        return this.removeMemberProvider;
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
