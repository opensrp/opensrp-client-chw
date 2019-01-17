package org.smartgresiter.wcaro.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.FamilyRegisterActivity;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.model.FamilyRemoveMemberModel;
import org.smartgresiter.wcaro.presenter.FamilyRemoveMemberPresenter;
import org.smartgresiter.wcaro.provider.FamilyRemoveMemberProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.Set;

public class FamilyRemoveMemberFragment extends BaseFamilyProfileMemberFragment implements FamilyRemoveMemberContract.View {

    String baseEntityId;
    String familyHead;
    String primaryCareGiver;

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
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver)  {
        FamilyRemoveMemberProvider provider = new FamilyRemoveMemberProvider(baseEntityId, this.getActivity(), this.commonRepository(), visibleColumns, new RemoveMemberListener(), new FooterListener(), familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter((Cursor) null, provider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(100);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        presenter = new FamilyRemoveMemberPresenter(this, new FamilyRemoveMemberModel(), null, baseEntityId, familyHead, primaryCareGiver);
    }

    protected FamilyRemoveMemberContract.Presenter getPresenter() {
        return (FamilyRemoveMemberContract.Presenter) presenter;
    }

    @Override
    public void removeMember(CommonPersonObjectClient client) {
        getPresenter().removeMember(client);
    }

    @Override
    public void displayChangeFamilyHeadDialog(final CommonPersonObjectClient client) {
        FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), baseEntityId,
                org.smartgresiter.wcaro.util.Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY);
        dialog.setOnSaveAndClose(new Runnable() {
            @Override
            public void run() {
                getPresenter().removeMember(client);
            }
        });
        dialog.show(getActivity().getFragmentManager(),"FamilyProfileChangeDialogHF");
    }

    @Override
    public void displayChangeCareGiverDialog(final CommonPersonObjectClient client) {
        FamilyProfileChangeDialog dialog = FamilyProfileChangeDialog.newInstance(getContext(), baseEntityId,
                org.smartgresiter.wcaro.util.Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);
        dialog.setOnSaveAndClose(new Runnable() {
            @Override
            public void run() {
                getPresenter().removeMember(client);
            }
        });

        dialog.show(getActivity().getFragmentManager(),"FamilyProfileChangeDialogPC");
    }

    @Override
    public void closeFamily() {

        getPresenter().removeEveryone();

    }

    @Override
    public void goToPrevious() {
        // open family register
        startActivity(new Intent(getContext(), FamilyRegisterActivity.class));
    }

    @Override
    public void startJsonActivity(JSONObject jsonObject) {
        Intent intent = new Intent(getContext(), Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonObject.toString());

        Form form = new Form();
        form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);
        form.setWizard(false);
        form.setSaveLabel("Remove");
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public class RemoveMemberListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(android.view.View v) {
            if (v.getTag(R.id.VIEW_ID) == BaseFamilyProfileMemberFragment.CLICK_VIEW_NORMAL) {
                CommonPersonObjectClient pc = (CommonPersonObjectClient) v.getTag();
                removeMember(pc);
            }
        }
    }

    public class FooterListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(android.view.View v) {

            closeFamily();
            Toast.makeText(getContext(), "Removing entire family", Toast.LENGTH_SHORT).show();
        }
    }


}
