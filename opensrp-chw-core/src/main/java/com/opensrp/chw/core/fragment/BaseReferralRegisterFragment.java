package com.opensrp.chw.core.fragment;

import android.view.View;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;
import com.opensrp.chw.core.provider.BasereferralRegisterProvider;

import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.HashMap;
import java.util.Set;

public abstract class BaseReferralRegisterFragment extends BaseChwRegisterFragment implements BaseReferralRegisterFragmentContract.View {

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        BasereferralRegisterProvider registerProvider = new BasereferralRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.menu_referrals;
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }
}
