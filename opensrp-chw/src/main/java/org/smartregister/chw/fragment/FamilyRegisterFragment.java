package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.CoreFamilyRegisterFragment;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.provider.ChwRegisterProvider;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class FamilyRegisterFragment extends CoreFamilyRegisterFragment {

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        CoreRegisterProvider chwRegisterProvider = new ChwRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        if (view.getId() == R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }
}
