package org.smartregister.chw.hf.fragment;

import android.os.Bundle;

import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.fragment.CoreFamilyProfileMemberFragment;
import org.smartregister.chw.core.provider.CoreMemberRegisterProvider;
import org.smartregister.chw.hf.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.hf.activity.FamilyProfileActivity;
import org.smartregister.chw.hf.model.FamilyProfileMemberModel;
import org.smartregister.chw.hf.provider.HfMemberRegisterProvider;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.presenter.BaseFamilyProfileMemberPresenter;
import org.smartregister.family.util.Constants;

import java.util.HashMap;
import java.util.Set;

public class FamilyProfileMemberFragment extends CoreFamilyProfileMemberFragment {

    public static BaseFamilyProfileMemberFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileMemberFragment fragment = new FamilyProfileMemberFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        CoreMemberRegisterProvider chwMemberRegisterProvider = new HfMemberRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, chwMemberRegisterProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return FamilyOtherMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return ChildProfileActivity.class;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return AncMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected boolean isAncMember(String baseEntityId) {
        return getFamilyProfileActivity().getFamilyProfilePresenter().isAncMember(baseEntityId);
    }

    @Override
    protected HashMap<String, String> getAncFamilyHeadNameAndPhone(String baseEntityId) {
        return getFamilyProfileActivity().getFamilyProfilePresenter().getAncFamilyHeadNameAndPhone(baseEntityId);
    }

    @Override
    protected CommonPersonObject getAncCommonPersonObject(String baseEntityId) {
        return getFamilyProfileActivity().getFamilyProfilePresenter().getAncCommonPersonObject(baseEntityId);
    }

    @Override
    protected boolean isPncMember(String baseEntityId) {
        return getFamilyProfileActivity().getFamilyProfilePresenter().isPncMember(baseEntityId);
    }

    @Override
    protected CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        return getFamilyProfileActivity().getFamilyProfilePresenter().getPncCommonPersonObject(baseEntityId);
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
        String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        presenter = new BaseFamilyProfileMemberPresenter(this, new FamilyProfileMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
    }

    private FamilyProfileActivity getFamilyProfileActivity() {
        return (FamilyProfileActivity) getActivity();
    }
}
