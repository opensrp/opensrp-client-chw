package com.opensrp.chw.hf.fragement;

import android.view.View;

import com.opensrp.chw.core.fragment.CoreFamilyRegisterFragment;
import com.opensrp.chw.core.provider.CoreRegisterProvider;
import com.opensrp.chw.hf.provider.HfRegisterProvider;
import com.opensrp.hf.R;

import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class FamilyRegisterFragment extends CoreFamilyRegisterFragment {

    @Override
    public void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getId() == R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        dueOnlyLayout.setVisibility(View.GONE);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        CoreRegisterProvider chwRegisterProvider = new HfRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }
}
