package org.smartregister.brac.hnpp.fragment;

import android.os.Bundle;

import org.smartregister.brac.hnpp.activity.AboveFiveChildProfileActivity;
import org.smartregister.brac.hnpp.activity.HnppChildProfileActivity;
import org.smartregister.brac.hnpp.activity.HnppFamilyOtherMemberProfileActivity;
import org.smartregister.brac.hnpp.model.HNPPFamilyProfileMemberModel;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.fragment.CoreFamilyProfileMemberFragment;
import org.smartregister.chw.core.provider.CoreMemberRegisterProvider;
import org.smartregister.brac.hnpp.provider.HNPPMemberRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.presenter.BaseFamilyProfileMemberPresenter;
import org.smartregister.family.util.Constants;

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
        CoreMemberRegisterProvider chwMemberRegisterProvider = new HNPPMemberRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, chwMemberRegisterProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
        String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        presenter = new BaseFamilyProfileMemberPresenter(this, new HNPPFamilyProfileMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return HnppFamilyOtherMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return HnppChildProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }
}
