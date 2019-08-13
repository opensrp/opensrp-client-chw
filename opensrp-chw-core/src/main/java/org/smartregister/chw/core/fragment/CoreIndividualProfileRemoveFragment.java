package org.smartregister.chw.core.fragment;

import android.content.Intent;

import com.opensrp.chw.core.R;
import org.smartregister.chw.core.activity.CoreAncRegisterActivity;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.activity.CoreIndividualProfileRemoveActivity;
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
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public abstract class CoreIndividualProfileRemoveFragment extends BaseFamilyProfileMemberFragment implements FamilyRemoveMemberContract.View {

    protected CoreFamilyRemoveMemberProvider removeMemberProvider;
    protected String familyBaseEntityId;
    protected CommonPersonObjectClient pc;
    protected String memberName;

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        setRemoveMemberProvider(visibleColumns, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, removeMemberProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(0);
        this.clientsView.setAdapter(this.clientAdapter);
        this.clientsView.setVisibility(android.view.View.GONE);
    }

    @Override
    protected String getMainCondition() {
        return "";
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

    protected abstract void setRemoveMemberProvider(Set visibleColumns, String familyHead, String primaryCaregiver);

    @Override
    protected void initializePresenter() {
        if (getArguments() != null) {
            familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
            String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
            String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
            pc = (CommonPersonObjectClient) getArguments().getSerializable(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON);
            setPresenter(familyHead, primaryCareGiver);
            openDeleteDialog();
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.d("setAdvancedSearchFormData");
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
        if (getActivity() != null && getActivity().getFragmentManager() != null) {
            CoreFamilyProfileChangeDialog dialog = getChangeFamilyHeadDialog();
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
            CoreFamilyProfileChangeDialog dialog = getChangeFamilyCareGiverDialog();
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
        if (getActivity() != null) {
            if (CoreConstants.EventType.REMOVE_FAMILY.equalsIgnoreCase(removalType)) {
                Intent intent = new Intent(getActivity(), getFamilyRegisterActivityClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                if (getActivity() != null) {
                    if (getActivity() instanceof CoreIndividualProfileRemoveActivity) {
                        CoreIndividualProfileRemoveActivity p = (CoreIndividualProfileRemoveActivity) getActivity();
                        p.onRemoveMember();
                    }
                }
            }
        }
    }

    @Override
    public void onEveryoneRemoved() {
        if (getActivity() != null && getActivity() instanceof CoreIndividualProfileRemoveActivity) {
            CoreIndividualProfileRemoveActivity p = (CoreIndividualProfileRemoveActivity) getActivity();
            p.onRemoveMember();
        }
    }

    protected abstract CoreFamilyProfileChangeDialog getChangeFamilyCareGiverDialog();

    protected abstract CoreFamilyProfileChangeDialog getChangeFamilyHeadDialog();

    protected abstract Class<? extends CoreFamilyRegisterActivity> getFamilyRegisterActivityClass();

    protected abstract Class<? extends CoreAncRegisterActivity> getAncRegisterActivityClass();

    protected abstract String getRemoveFamilyMemberDialogTag();

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
            dialog.show(getFragmentManager(), getRemoveFamilyMemberDialogTag());
            dialog.setOnRemove(new Runnable() {
                @Override
                public void run() {
                    getPresenter().processRemoveForm(form);
                    Intent intent = new Intent(getActivity(), getAncRegisterActivityClass());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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

}
