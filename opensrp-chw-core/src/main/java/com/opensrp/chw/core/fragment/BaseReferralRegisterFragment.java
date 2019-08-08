package com.opensrp.chw.core.fragment;

import android.database.Cursor;
import android.view.View;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;
import com.opensrp.chw.core.provider.BasereferralRegisterProvider;
import com.opensrp.chw.core.utils.CoreConstants;

import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Task;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.commonregistry.CommonFtsObject.searchTableName;

public abstract class BaseReferralRegisterFragment extends BaseChwRegisterFragment implements BaseReferralRegisterFragmentContract.View {

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns, String tableName) {
        BasereferralRegisterProvider registerProvider = new BasereferralRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(tablename));
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
        return "task.status = '" + Task.TaskStatus.READY.name() + "'";
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

    @Override
    protected void startRegistration() {//not used for referrals
    }

    @Override
    protected void onViewClicked(View view) {
        //TODO link to referral details
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void countExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query;
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                sql = sql.replace("WHERE", String.format("JOIN %s ON task.%s = %s.%s WHERE", CoreConstants.TABLE_NAME.TASK, CoreConstants.DB_CONSTANTS.FOR, searchTableName(tablename), CommonFtsObject.idColumn));
                Timber.i("FTS query %s", sql);

                clientAdapter.setTotalcount(commonRepository().countSearchIds(sql));
                Timber.v("total count here %s", clientAdapter.getTotalcount());


            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);

                Timber.i(query);
                c = commonRepository().rawCustomQueryForAdapter(query);
                c.moveToFirst();
                clientAdapter.setTotalcount(c.getInt(0));
                Timber.v("total count here %s", clientAdapter.getTotalcount());
            }

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

}
