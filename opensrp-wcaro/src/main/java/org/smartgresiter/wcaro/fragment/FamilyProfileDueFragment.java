package org.smartgresiter.wcaro.fragment;

import android.os.Bundle;

import org.smartgresiter.wcaro.presenter.FamilyProfileDuePresenter;
import org.smartgresiter.wcaro.provider.WcaroDueRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.model.BaseFamilyProfileDueModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;

import java.util.Set;

public class FamilyProfileDueFragment extends BaseFamilyProfileDueFragment {

    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new FamilyProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        presenter = new FamilyProfileDuePresenter(this, new BaseFamilyProfileDueModel(), null, familyBaseEntityId);
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        WcaroDueRegisterProvider wcaroDueRegisterProvider = new WcaroDueRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler);
        this.clientAdapter = new FamilyRecyclerViewCustomAdapter(null, wcaroDueRegisterProvider, this.context().commonrepository(this.tablename), Utils.metadata().familyDueRegister.showPagination);
        this.clientAdapter.setCurrentlimit(Utils.metadata().familyDueRegister.currentLimit);
        this.clientsView.setAdapter(this.clientAdapter);
    }

}
