package org.smartregister.chw.fragment;

import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.CoreFamilyRegisterFragment;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.provider.FamilyRegisterProvider;
import org.smartregister.chw.util.Utils;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.Set;

import timber.log.Timber;

public class FamilyRegisterFragment extends CoreFamilyRegisterFragment {

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        CoreRegisterProvider chwRegisterProvider = new FamilyRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
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

    @Override
    protected String dueFilterAndSortQuery() {

        String query = "";
        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
            sqb.addCondition(getFilterString(filters));
            sqb.addCondition(Utils.getFamilyDueFilter());
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String getFilterString(String filters){
        if(StringUtils.isBlank(filters))
            return "";

        return " and (" + CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FIRST_NAME +  " like '%" + filters + "%' or "
                + CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.LAST_NAME +  " like '%" + filters + "%')";
    }
}
